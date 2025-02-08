package com.gelsoncosta.gacademics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.data.api.ApiService
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MaterialViewModel(private val apiService: ApiService) : ViewModel() {

    private val _materials = MutableStateFlow<List<PdfMaterial>>(emptyList())
    val materials: StateFlow<List<PdfMaterial>> = _materials

    private val _selectedMaterial = MutableStateFlow<PdfMaterial?>(null)
    val selectedMaterial: StateFlow<PdfMaterial?> = _selectedMaterial

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMaterials()
                if (response.isSuccessful) {
                    _materials.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Erro ao carregar materiais"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchMyMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMyMaterials()
                if (response.isSuccessful) {
                    _materials.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Erro ao carregar materiais"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMaterialById(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getMaterial(materialId)
                if (response.isSuccessful) {
                    _selectedMaterial.value = response.body()
                } else {
                    _errorMessage.value = "Erro ao carregar material"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadMaterial(
        title: RequestBody,
        description: RequestBody,
        coverImage: MultipartBody.Part?,
        pdfFile: MultipartBody.Part,
        tags: RequestBody,
        category: RequestBody
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.uploadMaterial(title, description, coverImage, pdfFile, tags, category)
                if (response.isSuccessful) {
                    fetchMyMaterials() // Atualiza lista após envio
                } else {
                    _errorMessage.value = "Erro ao enviar material"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateMaterial(
        id: Int,
        title: RequestBody,
        description: RequestBody,
        coverImage: MultipartBody.Part?,
        pdfFile: MultipartBody.Part?,
        tags: RequestBody,
        category: RequestBody
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updateMaterial(id,title, description, coverImage, pdfFile, tags, category)
                if (response.isSuccessful) {
                    fetchMyMaterials()
                } else {
                    _errorMessage.value = "Erro ao enviar material"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMaterial(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.deleteMaterial(materialId)
                if (response.isSuccessful) {
                    _materials.value = _materials.value.filterNot { it.id == materialId }
                } else {
                    _errorMessage.value = "Erro ao excluir material"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
