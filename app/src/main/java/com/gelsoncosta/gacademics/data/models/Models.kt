package com.gelsoncosta.gacademics.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val profilePicture: String?
)

data class AuthResponse(
    val token: String,
    val user: User
)
@Entity(tableName = "materials")
data class PdfMaterial(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val title: String = "",
    val description: String ="",
    var cover: String? ="",
    var file_path: String ="",
    val tags: String ="",
    val category: String="",
    val creator_name: String="",
    val favorite_id:Int?=0
)


data class Comment(
    @SerializedName("id") val id: Int,
    @SerializedName("material_id") val materialId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?
)
data class CommentRequest(
    @SerializedName("material_id") val materialId: Int? = null,
    @SerializedName("content") val content: String
)
