package com.gelsoncosta.gacademics.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gelsoncosta.gacademics.data.models.PdfMaterial

@Dao
interface PdfMaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPdfMaterial(pdfMaterial: PdfMaterial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPdfMaterials(pdfMaterials: List<PdfMaterial>)

    @Update
    suspend fun updatePdfMaterial(pdfMaterial: PdfMaterial)

    @Delete
    suspend fun deletePdfMaterial(pdfMaterial: PdfMaterial)

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getPdfMaterialById(id: Int): PdfMaterial?

    @Query("SELECT * FROM materials")
    suspend fun getAllPdfMaterials(): List<PdfMaterial>

    @Query("SELECT * FROM materials WHERE title LIKE :searchQuery OR description LIKE :searchQuery OR tags LIKE :searchQuery OR category LIKE :searchQuery OR creator_name LIKE :searchQuery")
    suspend fun searchPdfMaterials(searchQuery: String): List<PdfMaterial>

    @Query("SELECT * FROM materials WHERE favorite_id = :favoriteId")
    suspend fun getFavoritePdfMaterials(favoriteId: Int): List<PdfMaterial>


    @Query("SELECT COUNT(*) FROM materials")
    suspend fun countPdfMaterials(): Int

    @Query("DELETE FROM materials")
    suspend fun deleteAllPdfMaterials()

    @Query("SELECT * FROM materials WHERE category = :category")
    suspend fun getPdfMaterialsByCategory(category: String): List<PdfMaterial>

    @Query("SELECT DISTINCT category FROM materials")
    suspend fun getAllCategories(): List<String>

}