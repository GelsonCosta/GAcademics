package com.gelsoncosta.gacademics.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gelsoncosta.gacademics.SigleConstVariables.academicCategories
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)
private val ErrorRed = Color(0xFFFF6B6B)



// Extension functions remain the same...
fun Uri.toFile(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this)
    val file = File(context.cacheDir, context.contentResolver.getFileName(this))
    FileOutputStream(file).use { outputStream ->
        inputStream?.copyTo(outputStream)
    }
    return file
}

fun android.content.ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayName = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (displayName != -1) {
                name = it.getString(displayName)
            }
        }
    }
    if (name.isEmpty()) {
        name = uri.lastPathSegment ?: "file"
    }
    return name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadMaterialScreen(
    viewModel: MaterialViewModel,
    onNavigateBack: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(academicCategories[0]) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // PDF picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPdfUri = uri
    }

    // Upload handling function remains the same...
    fun handleUpload() {
        if (selectedPdfUri == null) {
            errorMessage = "Please select a PDF file"
            return
        }

        isLoading = true
        errorMessage = null

        try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val tagsBody = tags.toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())

            val pdfFile = selectedPdfUri!!.toFile(context)
            val pdfRequestBody = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val pdfPart = MultipartBody.Part.createFormData(
                "file",
                pdfFile.name,
                pdfRequestBody
            )

            val imagePart = selectedImageUri?.let { uri ->
                val imageFile = uri.toFile(context)
                val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "cover",
                    imageFile.name,
                    imageRequestBody
                )
            }

            viewModel.uploadMaterial(
                title = titleBody,
                description = descriptionBody,
                coverImage = imagePart,
                pdfFile = pdfPart,
                tags = tagsBody,
                category = categoryBody
            )

            isLoading = false
            onUploadSuccess()
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error uploading material: ${e.message}"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header and other UI elements remain the same...
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Fazer Upload",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Upload Panels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Cover Image Upload Panel
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(
                            width = 1.dp,
                            color = AccentColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Cover Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adicionar Capa",
                                tint = AccentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Adicionar Capa",
                                color = TextGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // PDF Upload Panel
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(
                            width = 1.dp,
                            color = AccentColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            pdfPickerLauncher.launch("application/pdf")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (selectedPdfUri != null) Icons.Default.PictureAsPdf else Icons.Default.Add,
                            contentDescription = "Adicionar PDF",
                            tint = AccentColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            if (selectedPdfUri != null) "PDF Adicionado" else "Adicionar PDF",
                            color = TextGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição", color = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags (separadas por espaço)", color = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Category Dropdown
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria", color = TextGray) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    academicCategories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = TextWhite) },
                            onClick = {
                                category = option
                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message and upload button
            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = ErrorRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { handleUpload() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank()
                        && selectedPdfUri != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = TextWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextWhite
                    )
                } else {
                    Text(
                        "Fazer Upload",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}