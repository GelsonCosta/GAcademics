package com.gelsoncosta.gacademics.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gelsoncosta.gacademics.navigation.AppNavigator
import com.gelsoncosta.gacademics.ui.components.PdfMaterialDetailsCard
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailsScreen(
    viewModel: MaterialViewModel,
    materialId: Int,
    onNavigateBack: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val material by viewModel.selectedMaterial.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.fetchMaterialById(materialId)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .background(DarkBackground),
        containerColor = DarkBackground,
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Material Details",
                        color = TextWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextWhite
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(color = TextWhite)
            }

            errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            material?.let { pdfMaterial ->
                MaterialDetails(pdfMaterial)
            }
        }
    }
}

@Composable
private fun MaterialDetails(
    pdfMaterial: com.gelsoncosta.gacademics.data.models.PdfMaterial
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            PdfMaterialDetailsCard(
                pdfMaterial = pdfMaterial,
                onShareClick = { /* Implement share functionality */ },
                onBookmarkClick = { /* Implement bookmark functionality */ },
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "About this Material",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Text(
                    text = pdfMaterial.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                    color = TextGray,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
            }
        }
    }
}