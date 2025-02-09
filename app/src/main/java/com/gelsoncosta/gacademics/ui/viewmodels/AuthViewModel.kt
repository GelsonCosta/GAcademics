package com.gelsoncosta.gacademics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.data.api.ApiService
import com.gelsoncosta.gacademics.data.api.TokenManager
import com.gelsoncosta.gacademics.data.models.AuthResponse
import com.gelsoncosta.gacademics.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel(
    private val apiService: ApiService,
    private val dataStoreHelper: TokenManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val token = dataStoreHelper.getToken() ?: null
                val user = dataStoreHelper.getUser() ?: null
                if (token != null) {
                    _authToken.value = token
                    _user.value = user
                }
            }catch (e : Exception)
            {
                _errorMessage.value = "Erro: ${e.message}"
            }



        }
    }

    private fun fetchUser(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getUser()
                if (response.isSuccessful) {
                    _user.value = response.body()
                } else {
                    _errorMessage.value = "Falha ao obter dados do usuário"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.login(mapOf("email" to email, "password" to password))
                handleAuthResponse(response)
                if(response.isSuccessful){
                    onSuccess()
                }else{
                    onError( response.errorBody()?.string() ?: response.message())
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String,onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.register(mapOf("name" to name, "email" to email, "password" to password))
                handleAuthResponse(response)
                if(response.isSuccessful){
                    onSuccess()
                }else{
                    onError( response.errorBody()?.string() ?: response.message())
                }

            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
                onError("Erro: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun handleAuthResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { authResponse ->
                _authToken.value = authResponse.token
                _user.value = authResponse.user
                dataStoreHelper.saveToken(authResponse.token)
                dataStoreHelper.saveUser(authResponse.user)

            }
        } else {
            _errorMessage.value = "Falha na autenticação"
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreHelper.clearToken()
            dataStoreHelper.clearUser()
            _user.value = null
            _authToken.value = null
        }
    }
}
