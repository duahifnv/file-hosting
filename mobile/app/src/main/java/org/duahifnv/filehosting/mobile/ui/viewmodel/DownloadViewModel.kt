package org.duahifnv.filehosting.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.duahifnv.filehosting.mobile.data.api.ApiClient
import org.duahifnv.filehosting.mobile.data.api.ApiService
import org.duahifnv.filehosting.mobile.data.models.FileMetaDto
import java.util.UUID

class DownloadViewModel : ViewModel() {
    private val apiService: ApiService = ApiClient.apiService

    private val _fileMeta = MutableStateFlow<FileMetaDto?>(null)
    val fileMeta: StateFlow<FileMetaDto?> = _fileMeta.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _fileData = MutableStateFlow<ByteArray?>(null)
    val fileData: StateFlow<ByteArray?> = _fileData.asStateFlow()

    fun loadFileMeta(fileId: String, isShared: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            try {
                if (!isValidUUID(fileId)) {
                    _error.value = "Неверный формат идентификатора файла"
                    _loading.value = false
                    return@launch
                }

                val response = apiService.getFileMeta(fileId, if (isShared) true else null)
                if (response.isSuccessful) {
                    _fileMeta.value = response.body()
                    _error.value = null
                } else {
                    _error.value = when (response.code()) {
                        404 -> "Файл не найден"
                        410 -> "Срок действия файла истек"
                        401 -> "Не авторизован"
                        else -> "Ошибка загрузки метаданных"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun downloadFile(fileId: String, isShared: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            _fileData.value = null
            try {
                val response = apiService.getFileById(fileId, if (isShared) true else null)
                if (response.isSuccessful) {
                    _fileData.value = response.body()?.bytes()
                    _error.value = null
                } else {
                    _error.value = when (response.code()) {
                        404 -> "Файл не найден"
                        401 -> "Не авторизован"
                        else -> "Ошибка скачивания"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
        _fileMeta.value = null
    }

    private fun isValidUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: Exception) {
            false
        }
    }
}
