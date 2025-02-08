package com.gelsoncosta.gacademics.data.api

import android.content.SharedPreferences
import com.gelsoncosta.gacademics.SigleConstVariables
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenManager(private val preferences: SharedPreferences) {
    companion object {
        private const val KEY_TOKEN = "auth_token"
    }

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply()
    }
}

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip authentication for login and register endpoints
        if (originalRequest.url.encodedPath.endsWith("login") ||
            originalRequest.url.encodedPath.endsWith("register")) {
            return chain.proceed(originalRequest)
        }

        // Add token to all other requests
        val modifiedRequest = originalRequest.newBuilder()
            .header("Authorization", tokenManager.getToken() ?: "")
            .build()

        return chain.proceed(modifiedRequest)
    }
}

object RetrofitInstance {
    private const val BASE_URL = SigleConstVariables.BASE_URL
    private lateinit var tokenManager: TokenManager

    fun initialize(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}