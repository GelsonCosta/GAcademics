package com.gelsoncosta.gacademics.ui.screens

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gelsoncosta.gacademics.SigleConstVariables
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.math.max
import kotlin.math.min

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalPdfReaderScreen(
    viewModel: MaterialViewModel,
    id: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(0) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Zoom and pan state
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    // UI state
    var showControls by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }

    val material by viewModel.selectedMaterial.collectAsState()

    LaunchedEffect(id) {
        viewModel.getOfflineMaterialById(id)
    }

    // Use the local file path directly
    val pdfPath = material?.file_path ?: ""
    val title = material?.title ?: ""

    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var currentDocument by remember { mutableStateOf<ParcelFileDescriptor?>(null) }

    LaunchedEffect(pdfPath) {
        try {
            withContext(Dispatchers.IO) {
                val file = File(pdfPath)
                if (!file.exists()) {
                    throw Exception("File not found: $pdfPath")
                }

                val fileDescriptor = ParcelFileDescriptor.open(
                    file,
                    ParcelFileDescriptor.MODE_READ_ONLY
                )

                withContext(Dispatchers.Main) {
                    currentDocument = fileDescriptor
                    pdfRenderer = PdfRenderer(fileDescriptor)
                    totalPages = pdfRenderer?.pageCount ?: 0
                    renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                        currentBitmap = bitmap
                        isLoading = false
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                hasError = true
                isLoading = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            currentBitmap?.recycle()
            pdfRenderer?.close()
            currentDocument?.close()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar (only shown when not in fullscreen)
            AnimatedVisibility(
                visible = showControls && !isFullscreen,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                SmallTopAppBar(
                    title = {
                        Text(
                            text = title,
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = TextWhite
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isFullscreen = !isFullscreen }) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Alternar tela cheia",
                                tint = TextWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = DarkSurface
                    )
                )
            }

            // PDF Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { tapOffset ->
                                // Reset zoom on double tap
                                scale = if (scale != 1f) 1f else 2f
                                offset = Offset.Zero
                            },
                            onTap = {
                                showControls = !showControls
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 5f)
                            offset += pan
                        }
                    }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentColor
                    )
                } else if (hasError) {
                    ErrorContent(
                        onRetry = {
                            isLoading = true
                            hasError = false
                            scope.launch {
                                currentPage = 0
                                scale = 1f
                            }
                        }
                    )
                } else {
                    currentBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "PDF Page ${currentPage + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // Bottom Controls
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomControls(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPreviousPage = {
                        if (currentPage > 0) {
                            scope.launch {
                                currentPage--
                                renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                                    currentBitmap = bitmap
                                }
                            }
                        }
                    },
                    onNextPage = {
                        if (currentPage < totalPages - 1) {
                            scope.launch {
                                currentPage++
                                renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                                    currentBitmap = bitmap
                                }
                            }
                        }
                    },
                    onZoomIn = {
                        scale = (scale * 1.2f).coerceAtMost(5f)
                        scope.launch {
                            renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                                currentBitmap = bitmap
                            }
                        }
                    },
                    onZoomOut = {
                        scale = (scale * 0.8f).coerceAtLeast(0.5f)
                        scope.launch {
                            renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                                currentBitmap = bitmap
                            }
                        }
                    },
                    onResetZoom = {
                        scale = 1f
                        offset = Offset.Zero
                        scope.launch {
                            renderPage(currentPage, pdfRenderer, scale) { bitmap ->
                                currentBitmap = bitmap
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Erro",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Erro ao carregar o PDF",
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
        ) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun BottomControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(8.dp),
        color = DarkSurface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousPage,
                enabled = currentPage > 0
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateBefore,
                    contentDescription = "Página anterior",
                    tint = TextWhite
                )
            }

            Text(
                text = "${currentPage + 1}/$totalPages",
                color = TextWhite,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = onNextPage,
                enabled = currentPage < totalPages - 1
            ) {
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = "Próxima página",
                    tint = TextWhite
                )
            }

            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .padding(horizontal = 8.dp),
                color = TextGray
            )

            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Diminuir zoom",
                    tint = TextWhite
                )
            }

            IconButton(onClick = onResetZoom) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Resetar zoom",
                    tint = TextWhite
                )
            }

            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Aumentar zoom",
                    tint = TextWhite
                )
            }
        }
    }
}

private suspend fun renderPage(
    pageIndex: Int,
    pdfRenderer: PdfRenderer?,
    scale: Float,
    onBitmapReady: (Bitmap) -> Unit
) {
    withContext(Dispatchers.IO) {
        pdfRenderer?.let { renderer ->
            renderer.openPage(pageIndex).use { page ->
                val width = (page.width * scale).toInt()
                val height = (page.height * scale).toInt()

                val bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )

                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )

                withContext(Dispatchers.Main) {
                    onBitmapReady(bitmap)
                }
            }
        }
    }
}
