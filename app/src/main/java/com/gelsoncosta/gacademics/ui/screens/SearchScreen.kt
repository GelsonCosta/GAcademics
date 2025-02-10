package com.gelsoncosta.gacademics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import com.gelsoncosta.gacademics.navigation.AppNavigator
import com.gelsoncosta.gacademics.ui.components.GlideImage
import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MaterialViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val categoryResults by viewModel.categoryResults.collectAsState()
    val tagResults by viewModel.tagResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchTag by remember { mutableStateOf("") }

    val categories = listOf(
        "Matemática", "Física", "Química", "Biologia", "Ciência da Computação",
        "Engenharia", "Literatura", "História", "Geografia", "Filosofia",
        "Psicologia", "Economia", "Negócios", "Direito", "Medicina",
        "Artes", "Línguas", "Ciências Sociais", "Educação", "Outros"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesquisar", color = TextWhite) },
                navigationIcon = {
                    IconButton(onClick = { AppNavigator.navigatePopBack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.searchMaterials(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Pesquisar por título...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = TextGray)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.clearSearchResults()
                        }) {
                            Icon(Icons.Default.Clear, null, tint = TextGray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            // Categories
            Text(
                "Categorias",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = {
                            if (category == selectedCategory) {
                                selectedCategory = null
                                viewModel.clearSearchResults()
                            } else {
                                selectedCategory = category
                                viewModel.searchMaterialsByCategory(category)
                            }
                        },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentColor,
                            containerColor = DarkSurface
                        )
                    )
                }
            }

            // Tag Search
            OutlinedTextField(
                value = searchTag,
                onValueChange = {
                    searchTag = it
                    if (it.isNotEmpty()) {
                        viewModel.searchMaterialsByTag(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Pesquisar por tag...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Default.Tag, null, tint = TextGray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            // Results
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentColor
                    )
                }
            } else {
                val results = when {
                    searchResults.isNotEmpty() -> searchResults
                    categoryResults.isNotEmpty() -> categoryResults
                    tagResults.isNotEmpty() -> tagResults
                    else -> emptyList()
                }

                if (results.isEmpty() && (searchQuery.isNotEmpty() || selectedCategory != null || searchTag.isNotEmpty())) {
                    EmptySearchResult()
                } else {
                    SearchResults(
                        materials = results,
                        onMaterialClick = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResults(
    materials: List<PdfMaterial>,
    onMaterialClick: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(materials) { material ->
            MaterialSearchItem(
                material = material,
                onClick = { onMaterialClick(material.id) }
            )
        }
    }
}

@Composable
private fun MaterialSearchItem(
    material: PdfMaterial,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            material.cover?.let { coverUrl ->
                GlideImage(
                    imageUrl = coverUrl,
                    contentDescription = material.title,
                    modifier = Modifier
                        .size(60.dp)
                        .background(DarkBackground)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = material.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentColor
                )
                Text(
                    text = material.creator_name,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
private fun EmptySearchResult() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "Nenhum resultado encontrado",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}
