package com.gelsoncosta.gacademics.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.DownloadForOffline
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.gelsoncosta.gacademics.SigleConstVariables.BASE_URL
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import com.gelsoncosta.gacademics.navigation.AppNavigator
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Environment
import android.util.Log
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.gelsoncosta.gacademics.utils.downloadAndSaveFile
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.sql.DataSource

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)

@Composable
fun PdfMaterialDetailsCard(
    pdfMaterial: PdfMaterial,
    materialViewModel: MaterialViewModel,
) {
    val favoriteMaterials by materialViewModel.favoriteMaterials.collectAsState()
    val isFavorite = favoriteMaterials.any { it.id == pdfMaterial.id }
    val offlineMaterials by materialViewModel.Offlinematerials.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        materialViewModel.fetchFavorites()
        materialViewModel.getOfflineMaterials()
    }
    val isOfflineMaterial = offlineMaterials.any { it.id == pdfMaterial.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                pdfMaterial.cover?.let {
                    GlideImage(
                        imageUrl = it,
                        contentDescription = pdfMaterial.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground.copy(alpha = 0.5f))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            if(isOfflineMaterial){
                                coroutineScope.launch{
                                    materialViewModel.removePdfMaterial(pdfMaterial)
                                    AppNavigator.navigateToDetail(pdfMaterial.id)
                                }
                            }else{
                                coroutineScope.launch{
                                val savedPdf = downloadAndSaveFile(context, "${BASE_URL}${pdfMaterial.file_path}")
                                val savedCover = downloadAndSaveFile(context, "${BASE_URL}${pdfMaterial.cover}")
                                var pdf = ""
                                var img =""
                                savedPdf?.let {
                                    pdf = it
                                }
                                savedCover?.let{
                                    img = it
                                }
                            var newPdfMaterial = PdfMaterial(pdfMaterial.id, pdfMaterial.title, pdfMaterial.description,img,pdf,pdfMaterial.tags,pdfMaterial.category)

                                materialViewModel.insertPdfMaterial(newPdfMaterial)
                                AppNavigator.navigateToDetail(pdfMaterial.id)
                            }


                            }
                        },
                        modifier = Modifier.background(
                            DarkSurface.copy(alpha = 0.8f),
                            RoundedCornerShape(12.dp)
                        )
                    ) {
                        Icon(if(isOfflineMaterial) Icons.Default.DownloadDone else Icons.Default.DownloadForOffline, contentDescription = "Download", tint = TextWhite)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (isFavorite) {
                                materialViewModel.removeFromFavorites(pdfMaterial.id)
                            } else {
                                materialViewModel.addToFavorites(pdfMaterial.id)
                            }
                        },
                        modifier = Modifier.background(
                            DarkSurface.copy(alpha = 0.8f),
                            RoundedCornerShape(12.dp)
                        )
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.BookmarkBorder else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) AccentColor else TextWhite
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = pdfMaterial.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = pdfMaterial.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = TextWhite.copy(alpha = 0.1f)
                )

                PdfMaterialTagChips(pdfMaterial.tags)

                Button(
                    onClick = { AppNavigator.navigateToPdfReader(pdfMaterial.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentColor
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = "Read Icon", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ler", style = MaterialTheme.typography.titleMedium)
                }

            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PdfMaterialTagChips(tags: String) {
    val tagList = tags.split(",", " ")
        .filter { it.isNotBlank() }
        .distinct()

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = Int.MAX_VALUE
    ) {
        tagList.forEach { tag ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tag.trim(),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextWhite
                    )
                },
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = AccentColor.copy(alpha = 0.2f),
                    labelColor = AccentColor
                )
            )
        }
    }
}




@Composable
fun GlideImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    isOffline: Boolean = false
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = modifier,
        update = { imageView ->
            val imageSource = if (isOffline) {
                File(imageUrl)
            } else {
                "$BASE_URL$imageUrl"
            }

            Glide.with(context)
                .load(imageSource)
                .centerCrop()
                .into(imageView)
        }
    )
}



fun downloadFile(context: Context, fileUrl: String, title: String): String? {
    var connection: HttpURLConnection? = null
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null

    try {
        // Criar diretório se não existir
        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "GAcademics")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        // Definir nome do arquivo
        val extension = fileUrl.substringAfterLast(".", "pdf")
        val fileName = "$title.$extension"
        val file = File(storageDir, fileName)

        // Configurar e abrir conexão
        connection = URL(fileUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        // Baixar e salvar o arquivo
        inputStream = connection.inputStream
        outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        return file.absolutePath

    } catch (e: Exception) {
        Log.e("Download", "Erro ao baixar arquivo: ${e.message}")
        e.printStackTrace()
        return null
    } finally {
        outputStream?.close()
        inputStream?.close()
        connection?.disconnect()
    }
}
