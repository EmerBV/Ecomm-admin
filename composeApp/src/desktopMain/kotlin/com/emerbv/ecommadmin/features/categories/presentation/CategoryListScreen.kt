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
import com.emerbv.ecommadmin.core.ui.components.MainLayout
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import org.koin.java.KoinJavaComponent.get

@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel,
    userData: JwtResponse,
    navigationState: NavigationState,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<CategoryDto?>(null) }
    val tokenManager: TokenManager = get(TokenManager::class.java)

    // Snackbar host state for showing success/error messages
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message when available
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.successMessage)
            viewModel.clearMessages()
        }
    }

    // Load categories when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    EcommAdminTheme {
        MainLayout(
            currentRoute = "categories",
            onNavigate = { route ->
                when (route) {
                    "dashboard" -> navigationState.navigateTo(Screen.Dashboard(userData))
                    "products" -> navigationState.navigateTo(Screen.ProductList(userData))
                    "categories" -> {} // Ya estamos en categorías
                    "orders" -> {} // Implementar navegación a órdenes
                    "users" -> {} // Implementar navegación a usuarios
                    "settings" -> {} // Implementar navegación a configuración
                }
            },
            userName = "Admin", // Idealmente obtener del usuario actual
            onLogout = {
                tokenManager.clearSession()
                navigationState.navigateTo(Screen.Login)
            },
            title = "Categories",
            topBarActions = {
                IconButton(onClick = { viewModel.loadCategories() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            },
            snackbarHostState = snackbarHostState
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Show loading indicator
                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.h5,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Table header and content
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

                            // Content area with appropriate state handling
                            if (uiState.isLoading && uiState.categories.isEmpty()) {
                                // Initial loading
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (uiState.errorMessage.isNotEmpty()) {
                                // Error state
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colors.error,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = uiState.errorMessage,
                                            color = MaterialTheme.colors.error,
                                            style = MaterialTheme.typography.body1,
                                            textAlign = TextAlign.Center
                                        )
                                        Button(
                                            onClick = { viewModel.loadCategories() },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = MaterialTheme.colors.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Retry",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Retry")
                                        }
                                    }
                                }
                            } else if (uiState.categories.isEmpty()) {
                                // Empty state
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No categories found",
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                // Categories list
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(
                                        items = uiState.categories,
                                        key = { it.id }
                                    ) { category ->
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

                // Delete confirmation dialog
                if (showDeleteDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = null },
                        title = { Text("Delete Category") },
                        text = {
                            Text(
                                "Are you sure you want to delete the category '${showDeleteDialog?.name}'? " +
                                        "This action cannot be undone."
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDeleteDialog?.id?.let {
                                        viewModel.deleteCategory(it)
                                    }
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
    }
}

@Composable
fun CategoryRow(
    category: CategoryDto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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