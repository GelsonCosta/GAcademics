package com.gelsoncosta.gacademics.ui.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.data.api.ApiService
import com.gelsoncosta.gacademics.data.models.Comment
import com.gelsoncosta.gacademics.data.models.CommentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(private val apiService: ApiService) : ViewModel() {
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchComments(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.getComments(materialId)
                if (response.isSuccessful) {
                    _comments.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Falha ao carregar comentários: ${response.code()}${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro ao carregar comentários"
                _comments.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addComment(materialId: Int, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val commentRequest = CommentRequest(
                    materialId = materialId,
                    content = content
                )
                val response = apiService.addComment(commentRequest)
                if (response.isSuccessful) {
                    response.body()?.let { newComment ->
                        _comments.value = _comments.value + newComment
                    }
                } else {
                    _errorMessage.value = "Falha ao adicionar comentário: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro ao adicionar comentário"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editComment(commentId: Int, content: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val commentRequest = CommentRequest(content = content)
                val response = apiService.updateComment(commentId, commentRequest)
                if (response.isSuccessful) {
                    response.body()?.let { updatedComment ->
                        _comments.value = _comments.value.map {
                            if (it.id == commentId) updatedComment else it
                        }
                    }
                } else {
                    _errorMessage.value = "Falha ao editar comentário: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro ao editar comentário"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.deleteComment(commentId)
                if (response.isSuccessful) {
                    _comments.value = _comments.value.filterNot { it.id == commentId }
                } else {
                    _errorMessage.value = "Falha ao excluir comentário: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro ao excluir comentário"
            } finally {
                _isLoading.value = false
            }
        }
    }
}