package com.gelsoncosta.gacademics.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileDownloader(private val context: Context) {
    private val client = OkHttpClient()

    suspend fun downloadFile(fileUrl: String, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            // Criar pasta
            val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "GAcademics")
            folder.mkdirs()

            // Criar arquivo
            val file = File(folder, fileName)

            // Criar request
            val request = Request.Builder()
                .url(fileUrl)
                .build()

            // Executar request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("Download", "Resposta não sucedida: ${response.code}")
                    return@withContext null
                }

                // Pegar o body e salvar no arquivo
                response.body?.let { body ->
                    FileOutputStream(file).use { fos ->
                        fos.write(body.bytes())
                    }
                    return@withContext file.absolutePath
                } ?: run {
                    Log.e("Download", "Body vazio")
                    return@withContext null
                }
            }
        } catch (e: Exception) {
            Log.e("Download", "Erro no download", e)
            return@withContext null
        }
    }
}

// Uso da classe:
suspend fun downloadAndSaveFile(context: Context, url: String): String? {
    val downloader = FileDownloader(context)
    val fileName = url.substringAfterLast("/")
    return downloader.downloadFile(url, fileName)
}