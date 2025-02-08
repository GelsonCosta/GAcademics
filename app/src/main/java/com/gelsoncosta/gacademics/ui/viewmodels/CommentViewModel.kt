package com.gelsoncosta.gacademics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.data.api.ApiService
import com.gelsoncosta.gacademics.data.models.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class CommentViewModel(private val apiService: ApiService) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchComments(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getComments(materialId)
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Erro ao carregar comentários"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addComment(materialId: Int, text: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.addComment(materialId, mapOf("text" to text))
                if (response.isSuccessful) {
                    response.body()?.let { newComment ->
                        _comments.value = _comments.value + newComment
                    }
                } else {
                    _errorMessage.value = "Erro ao adicionar comentário"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteComment(commentId)
                if (response.isSuccessful) {
                    _comments.value = _comments.value.filterNot { it.id == commentId }
                } else {
                    _errorMessage.value = "Erro ao excluir comentário"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
