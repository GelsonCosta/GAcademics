package com.gelsoncosta.gacademics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.gelsoncosta.gacademics.navigation.AppNavigator
import com.gelsoncosta.gacademics.ui.components.GlideImage

import com.gelsoncosta.gacademics.ui.viewmodel.MaterialViewModel
import kotlinx.coroutines.launch

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun FavoritelListScreen(
    viewModel: MaterialViewModel,
    onNavigateToDetail: (Int) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val materials by viewModel.favoriteMaterials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val search = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = DarkSurface
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "G-Academics",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextWhite
                )
                Divider(color = TextGray.copy(alpha = 0.2f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { AppNavigator.navigateToHome() },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = DarkSurface,
                        unselectedIconColor = TextWhite,
                        unselectedTextColor = TextWhite
                    )
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Book, null) },
                    label = { Text("My Books") },
                    selected = false,
                    onClick = { AppNavigator.navigateToMyMaterials() },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = DarkSurface,
                        unselectedIconColor = TextWhite,
                        unselectedTextColor = TextWhite
                    )
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = { AppNavigator.navigateToFavorites() },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = DarkSurface,
                        unselectedIconColor = TextWhite,
                        unselectedTextColor = TextWhite
                    )
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Download, null) },
                    label = { Text("Downloads") },
                    selected = false,
                    onClick = { AppNavigator.navigateToDownloads() },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = DarkSurface,
                        unselectedIconColor = TextWhite,
                        unselectedTextColor = TextWhite
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, null) },
                    label = { Text("Sign Out") },
                    selected = false,
                    onClick = {AppNavigator.onSignOut()},
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = DarkSurface,
                        unselectedIconColor = Color(0xFFFF6B6B),
                        unselectedTextColor = Color(0xFFFF6B6B)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.background(DarkBackground),
            containerColor = DarkBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Favoritos",
                            color = TextWhite,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = TextWhite
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.fetchFavorites() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = TextWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground,
                        titleContentColor = TextWhite
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = AccentColor
                        )
                    }
                    errorMessage != null -> {
                        ErrorState(
                            errorMessage = errorMessage!!,
                            onRetry = { viewModel.fetchFavorites() }
                        )
                    }
                    materials.isEmpty() -> {
                        EmptyState(
                            onRefresh = { viewModel.fetchFavorites() }
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground)
                        ) {
                            Text(
                                text = "Os teus materiais \nfavoritos aqui!",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Start,
                                color = TextWhite,
                                maxLines = 2,
                                modifier = Modifier.padding(start = 16.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = search.value,
                                onValueChange = { search.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                placeholder = { Text("Pesquisar Conteúdos", color = TextGray) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = TextGray
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentColor,
                                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = AccentColor
                                ),
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium
                            )

                            Text(
                                text = "Materiais Académicos",
                                style = MaterialTheme.typography.titleMedium,
                                color = AccentColor,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                            )

                            MaterialList(
                                materials = materials.filter {
                                    it.title.contains(search.value, ignoreCase = true)
                                },
                                materialViewModel = viewModel,
                                onNavigateToDetail = onNavigateToDetail
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaterialList(
    materials: List<com.gelsoncosta.gacademics.data.models.PdfMaterial>,
    materialViewModel : MaterialViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(materials.reversed()) { material ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDetail(material.id) },
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                ),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    material.cover?.let { coverUrl ->
                        GlideImage(
                            imageUrl = coverUrl,
                            contentDescription = material.title,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(MaterialTheme.shapes.small)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row {
                            Text(
                                text = material.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = material.creator_name,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                        }

                        Row {
                            Text(
                                text = "${material.description.take(25)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    if (material.favorite_id != null) {
                                        materialViewModel.removeFromFavorites(material.id)
                                    } else {
                                        materialViewModel.addToFavorites(material.id)
                                    }
                                },
                                modifier = Modifier.background(
                                    DarkSurface.copy(alpha = 0.8f),
                                    RoundedCornerShape(12.dp)
                                )
                            ) {
                                Icon(
                                    imageVector = if (material.favorite_id != null) Icons.Filled.BookmarkBorder else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (material.favorite_id != null) AccentColor else TextWhite
                                )
                            }
                        }
                        Text(
                            text = material.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color(0xFFFF6B6B),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFFF6B6B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = TextWhite
            )
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No materials available",
            style = MaterialTheme.typography.titleMedium,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentColor,
                contentColor = TextWhite
            )
        ) {
            Text("Refresh")
        }
    }
}