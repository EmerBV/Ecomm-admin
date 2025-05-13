package com.emerbv.ecommadmin.features.categories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto

@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel,
    userData: JwtResponse,
    navigationState: NavigationState,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<CategoryDto?>(null) }

    // Cargar categorías al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    // Debug - Mostrar información sobre el estado
    LaunchedEffect(uiState) {
        println("UI State actualizado: ${uiState.categories.size} categorías, isLoading=${uiState.isLoading}, error=${uiState.errorMessage}")
    }

    EcommAdminTheme {
        // Eliminada la Row que contenía el sidebar, ahora usamos solo la columna de contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
                actions = {
                    IconButton(onClick = { viewModel.loadCategories() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )

            // Main content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Manage your product categories",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = { navigationState.navigateTo(Screen.CategoryAdd(userData)) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Category")
                        }
                    }

                    // Table header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Table header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.surface)
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ID",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(50.dp)
                                )
                                Text(
                                    text = "Category Name",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(3f)
                                )
                                Text(
                                    text = "Products",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Actions",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider()

                            // Content area
                            if (uiState.isLoading && uiState.categories.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (uiState.errorMessage.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colors.error
                                        )
                                        Text(
                                            text = uiState.errorMessage,
                                            color = MaterialTheme.colors.error,
                                            style = MaterialTheme.typography.body2
                                        )
                                        Button(
                                            onClick = { viewModel.loadCategories() },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = MaterialTheme.colors.primary
                                            )
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            } else if (uiState.categories.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No categories found",
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                // Category list - Altura fija para evitar problemas de renderizado
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 200.dp)
                                ) {
                                    LazyColumn {
                                        items(uiState.categories) { category ->
                                            CategoryRow(
                                                category = category,
                                                onEditClick = {
                                                    navigationState.navigateTo(
                                                        Screen.CategoryEdit(userData, category)
                                                    )
                                                },
                                                onDeleteClick = {
                                                    showDeleteDialog = category
                                                }
                                            )
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Category") },
                text = { Text("Are you sure you want to delete the category '${showDeleteDialog?.name}'? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog?.id?.let { viewModel.deleteCategory(it) }
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryRow(
    category: CategoryDto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Para fines de depuración
    println("Renderizando categoría: ${category.id} - ${category.name}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID
        Text(
            text = category.id.toString(),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.width(50.dp)
        )

        // Category Name with Image
        Row(
            modifier = Modifier.weight(3f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category image or placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.take(1).uppercase(),
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Category name
            Text(
                text = category.name,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )
        }

        // Products count (placeholder since we don't have actual count)
        Text(
            text = "0",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        // Actions
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD32F2F), // Red color
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}