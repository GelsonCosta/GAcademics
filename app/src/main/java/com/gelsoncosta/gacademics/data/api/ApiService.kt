package com.gelsoncosta.gacademics.data.api

import com.gelsoncosta.gacademics.data.models.AuthResponse
import com.gelsoncosta.gacademics.data.models.Comment
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import com.gelsoncosta.gacademics.data.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.Objects

interface ApiService {
    // Authentication endpoints don't need @Header annotation
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body user: Map<String, String>): Response<AuthResponse>

    // All other endpoints will automatically get the Authorization header from the interceptor
    @GET("users/profile")
    suspend fun getUser(): Response<User>

    @Multipart
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): Response<User>

    @GET("materials")
    suspend fun getMaterials(): Response<List<PdfMaterial>>

    @GET("my-materials")
    suspend fun getMyMaterials(): Response<List<PdfMaterial>>

    @GET("materials/{id}")
    suspend fun getMaterial(@Path("id") id: Int): Response<PdfMaterial>

    @Multipart
    @POST("materials")
    suspend fun uploadMaterial(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part,
        @Part("tags") tags: RequestBody,
        @Part("category") category: RequestBody
    ): Response<Any>

    @Multipart
    @PUT("materials/{id}")
    suspend fun updateMaterial(
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part cover: MultipartBody.Part?,
        @Part file: MultipartBody.Part?,
        @Part("tags") tags: RequestBody,
        @Part("category") category: RequestBody
    ): Response<Any>

    @DELETE("materials/{id}")
    suspend fun deleteMaterial(@Path("id") id: Int): Response<Unit>

    @GET("materials/{id}/comments")
    suspend fun getComments(@Path("id") materialId: Int): Response<List<Comment>>

    @POST("materials/{id}/comments")
    suspend fun addComment(
        @Path("id") materialId: Int,
        @Body comment: Map<String, String>
    ): Response<Comment>

    @DELETE("comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: Int): Response<Unit>

    // Favoritos
    @POST("favorites")
    suspend fun addToFavorites(@Body materialId: Map<String, Int>): Response<Any>

    @DELETE("favorites/{material_id}")
    suspend fun removeFromFavorites(@Path("material_id") materialId: Int): Response<Unit>

    @GET("favorites")
    suspend fun getFavorites(): Response<List<PdfMaterial>>
}