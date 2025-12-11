package org.duahifnv.filehosting.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.duahifnv.filehosting.mobile.data.api.ApiClient
import org.duahifnv.filehosting.mobile.data.api.ApiService
import org.duahifnv.filehosting.mobile.data.models.FileMetaDto

class FilesViewModel : ViewModel() {
    private val apiService: ApiService = ApiClient.apiService

    private val _myFiles = MutableStateFlow<List<FileMetaDto>>(emptyList())
    val myFiles: StateFlow<List<FileMetaDto>> = _myFiles.asStateFlow()

    private val _sharedFiles = MutableStateFlow<List<FileMetaDto>>(emptyList())
    val sharedFiles: StateFlow<List<FileMetaDto>> = _sharedFiles.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var currentPage = 0
    var currentSize = 20
    var currentSort: String? = null
    var currentSortDirection: String? = null
    var currentContentType: String? = null

    fun loadMyFiles(
        contentType: String? = null,
        sort: String? = null,
        sortDirection: String? = null,
        page: Int = 0,
        size: Int = 20
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                currentPage = page
                currentSize = size
                currentSort = sort
                currentSortDirection = sortDirection
                currentContentType = contentType

                val response = apiService.getAllFileMetas(
                    contentType = contentType,
                    shared = false,
                    page = page,
                    size = size,
                    sort = sort,
                    sortDirection = sortDirection
                )
                if (response.isSuccessful) {
                    _myFiles.value = response.body()?.fileMetas ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Ошибка загрузки файлов"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadSharedFiles(
        contentType: String? = null,
        sort: String? = null,
        sortDirection: String? = null,
        page: Int = 0,
        size: Int = 20
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getAllFileMetas(
                    contentType = contentType,
                    shared = true,
                    page = page,
                    size = size,
                    sort = sort,
                    sortDirection = sortDirection
                )
                if (response.isSuccessful) {
                    _sharedFiles.value = response.body()?.fileMetas ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Ошибка загрузки файлов"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteFile(fileId: String, isShared: Boolean = false) {
        viewModelScope.launch {
            try {
                val response = apiService.removeFile(fileId)
                if (response.isSuccessful) {
                    if (isShared) {
                        loadSharedFiles(
                            currentContentType,
                            currentSort,
                            currentSortDirection,
                            currentPage,
                            currentSize
                        )
                    } else {
                        loadMyFiles(
                            currentContentType,
                            currentSort,
                            currentSortDirection,
                            currentPage,
                            currentSize
                        )
                    }
                    _error.value = null
                } else {
                    _error.value = "Ошибка удаления файла"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            }
        }
    }

    fun uploadFile(file: MultipartBody.Part) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.uploadFile(file)
                if (response.isSuccessful) {
                    loadMyFiles(
                        currentContentType,
                        currentSort,
                        currentSortDirection,
                        currentPage,
                        currentSize
                    )
                    _error.value = null
                } else {
                    _error.value = "Ошибка загрузки файла"
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
    }
}
