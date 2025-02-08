package com.gelsoncosta.gacademics.data.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val profilePicture: String?
)

data class AuthResponse(
    val token: String,
)

data class PdfMaterial(
    val id: Int,
    val title: String,
    val description: String,
    val cover: String?,
    val file_path: String,
    val tags: String,
    val category: String,
    val creator_name: String
)


data class Comment(
    val id: Int,
    val userId: Int,
    val materialId: Int,
    val text: String,
    val createdAt: String
)
