package org.duahifnv.filehosting.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.duahifnv.filehosting.mobile.data.TokenStore
import org.duahifnv.filehosting.mobile.data.api.ApiClient
import org.duahifnv.filehosting.mobile.data.api.ApiService
import org.duahifnv.filehosting.mobile.data.models.*

class AuthViewModel(private val tokenStore: TokenStore) : ViewModel() {
    private val apiService: ApiService = ApiClient.apiService

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _userForm = MutableStateFlow<UserFormDto?>(null)
    val userForm: StateFlow<UserFormDto?> = _userForm.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStore.token.collect { token ->
                _isAuthenticated.value = !token.isNullOrEmpty()
                if (!token.isNullOrEmpty()) {
                    ApiClient.setToken(token)
                    loadUserForm()
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.authenticate(AuthDto(username, password))
                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        tokenStore.saveToken(token)
                        ApiClient.setToken(token)
                    }
                    _error.value = null
                } else {
                    _error.value = "Неверные учетные данные"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            }
        }
    }

    fun register(registerDto: RegisterDto) {
        viewModelScope.launch {
            try {
                val response = apiService.register(registerDto)
                if (response.isSuccessful) {
                    response.body()?.token?.let { token ->
                        tokenStore.saveToken(token)
                        ApiClient.setToken(token)
                    }
                    _error.value = null
                } else {
                    _error.value = "Ошибка регистрации"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken()
            ApiClient.setToken(null)
            _userForm.value = null
        }
    }

    private fun loadUserForm() {
        viewModelScope.launch {
            try {
                val response = apiService.getUserForm()
                if (response.isSuccessful) {
                    _userForm.value = response.body()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun updateUser(userFormDto: UserFormDto) {
        viewModelScope.launch {
            try {
                val response = apiService.updateUser(userFormDto)
                if (response.isSuccessful) {
                    loadUserForm()
                    _error.value = null
                } else {
                    _error.value = "Ошибка обновления"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка подключения: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
