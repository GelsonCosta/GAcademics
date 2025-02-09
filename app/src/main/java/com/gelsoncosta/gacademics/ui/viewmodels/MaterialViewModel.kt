package com.gelsoncosta.gacademics.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.data.api.ApiService
import com.gelsoncosta.gacademics.data.local.dao.PdfMaterialDao
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MaterialViewModel(private val apiService: ApiService, private val pdfMaterialDao: PdfMaterialDao) : ViewModel() {

    private val _materials = MutableStateFlow<List<PdfMaterial>>(emptyList())
    val materials: StateFlow<List<PdfMaterial>> = _materials

    private val _Offlinematerials = MutableStateFlow<List<PdfMaterial>>(emptyList())
    val Offlinematerials: StateFlow<List<PdfMaterial>> = _Offlinematerials

    private val _Mymaterials = MutableStateFlow<List<PdfMaterial>>(emptyList())
    val Mymaterials: StateFlow<List<PdfMaterial>> = _Mymaterials

    private val _selectedMaterial = MutableStateFlow<PdfMaterial?>(null)
    val selectedMaterial: StateFlow<PdfMaterial?> = _selectedMaterial

    private val _favoriteMaterials = MutableStateFlow<List<PdfMaterial>>(emptyList())
    val favoriteMaterials: StateFlow<List<PdfMaterial>> = _favoriteMaterials

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
                    _errorMessage.value = null
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
                    _Mymaterials.value = response.body() ?: emptyList()
                    _errorMessage.value = null
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
                val response = apiService.updateMaterial(id, title, description, coverImage, pdfFile, tags, category)
                if (response.isSuccessful) {
                    fetchMyMaterials()
                } else {
                    _errorMessage.value = "Erro ao atualizar material"
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

    // ⭐ FAVORITES FUNCTIONS ⭐

    fun fetchFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getFavorites()
                if (response.isSuccessful) {
                    _favoriteMaterials.value = response.body() ?: emptyList()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Erro ao carregar favoritos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToFavorites(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.addToFavorites(mapOf("material_id" to materialId))
                if (response.isSuccessful) {
                    fetchFavorites() // Atualiza a lista de favoritos após a adição
                    fetchMaterials()
                } else {
                    _errorMessage.value = "Erro ao adicionar aos favoritos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromFavorites(materialId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.removeFromFavorites(materialId)
                if (response.isSuccessful) {
                    _favoriteMaterials.value = _favoriteMaterials.value.filterNot { it.id == materialId }
                    fetchMaterials()
                } else {
                    _errorMessage.value = "Erro ao remover dos favoritos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Pdfs Offlines
    fun insertPdfMaterial(pdfMaterial: PdfMaterial) {
        viewModelScope.launch {
            pdfMaterialDao.insertPdfMaterial(pdfMaterial)
            getOfflineMaterials()
        }
    }
    suspend fun getOfflineMaterialById(id: Int): PdfMaterial? {
        _selectedMaterial.value = pdfMaterialDao.getPdfMaterialById(id)
        return _selectedMaterial.value
    }

    fun removePdfMaterial(pdfMaterial: PdfMaterial) {
        viewModelScope.launch {
            pdfMaterialDao.deletePdfMaterial(pdfMaterial)
            getOfflineMaterials()
        }
    }

    fun getOfflineMaterials() {
        viewModelScope.launch {
            _Offlinematerials.value = pdfMaterialDao.getAllPdfMaterials()
        }
    }

}
