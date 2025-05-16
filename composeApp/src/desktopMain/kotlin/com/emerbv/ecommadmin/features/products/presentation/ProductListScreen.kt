package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.navigation.NavigationState
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.ui.components.MainLayout
import com.emerbv.ecommadmin.core.ui.components.Chip
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.products.data.model.ProductDto

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    userData: JwtResponse,
    navigationState: NavigationState,
    tokenManager: TokenManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado para menús y búsqueda
    var showSortMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    // Estado para el diálogo de confirmación de eliminación
    var showDeleteDialog by remember { mutableStateOf<ProductDto?>(null) }

    // Mostrar mensaje de éxito
    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    // Cargar datos cuando aparece la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

    // Observar errores y mostrar snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Actualizar la búsqueda cuando cambia
    LaunchedEffect(searchQuery) {
        viewModel.updateSearchQuery(searchQuery)
    }

    EcommAdminTheme {
        MainLayout(
            currentRoute = "products",
            onNavigate = { route ->
                when (route) {
                    "dashboard" -> navigationState.navigateTo(Screen.Dashboard(userData))
                    "products" -> {} // Ya estamos en productos
                    "categories" -> navigationState.navigateTo(Screen.CategoryList(userData))
                    "orders" -> {} // Implementar navegación a órdenes
                    "users" -> {} // Implementar navegación a usuarios
                    "settings" -> {} // Implementar navegación a configuración
                }
            },
            userName = "Admin", // Idealmente obtener del usuario actual
            onLogout = { },
            title = if (isSearchVisible) "" else "Products",
            topBarActions = {
                if (isSearchVisible) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        placeholder = { Text("Search products...") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                            focusedIndicatorColor = MaterialTheme.colors.primary,
                            unfocusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            cursorColor = MaterialTheme.colors.primary
                        ),
                        leadingIcon = {
                            IconButton(onClick = {
                                isSearchVisible = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.ArrowBack, "Back")
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, "Clear search")
                                }
                            }
                        }
                    )
                } else {
                    // Botones de la barra superior
                    IconButton(onClick = { isSearchVisible = true }) {
                        Icon(Icons.Default.Search, "Search")
                    }

                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }

                    IconButton(onClick = { showCategoryMenu = true }) {
                        Icon(Icons.Default.Category, "Filter by category")
                    }

                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, "Sort")
                    }
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
                    // Indicador de carga
                    if (uiState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Encabezado con título y botón de agregar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Products",
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Manage your product catalog",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = { navigationState.navigateTo(Screen.ProductAdd(userData)) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Product")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros aplicados (mostrar si hay alguno)
                    if (uiState.selectedCategory != null) {
                        Row(
                            modifier = Modifier.padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filters: ",
                                style = MaterialTheme.typography.body2,
                                fontWeight = FontWeight.Bold
                            )
                            Chip(
                                onClick = { viewModel.filterByCategory(null) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = "Category",
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            ) {
                                Text(uiState.selectedCategory ?: "")
                            }
                        }
                    }

                    // Tabla de productos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Encabezado de la tabla
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
                                    modifier = Modifier.width(40.dp)
                                )
                                Text(
                                    text = "Product",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(3f)
                                )
                                Text(
                                    text = "Category",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "Price",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Inventory",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Status",
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

                            // Contenido de la tabla según el estado
                            if (uiState.isLoading && uiState.products.isEmpty()) {
                                // Carga inicial
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (uiState.errorMessage != null && uiState.products.isEmpty()) {
                                // Error al cargar productos
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colors.error
                                        )
                                        Text(
                                            text = uiState.errorMessage ?: "Unknown error",
                                            style = MaterialTheme.typography.body1,
                                            color = MaterialTheme.colors.error,
                                            textAlign = TextAlign.Center
                                        )
                                        Button(
                                            onClick = { viewModel.refreshData() },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Retry",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Retry")
                                        }
                                    }
                                }
                            } else if (uiState.filteredProducts.isEmpty()) {
                                // Sin productos para mostrar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No products found",
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                // Lista de productos
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(
                                        items = uiState.filteredProducts,
                                        key = { it.id }
                                    ) { product ->
                                        ProductRow(
                                            product = product,
                                            onProductClick = {
                                                navigationState.navigateTo(Screen.ProductDetail(userData, product))
                                            },
                                            onEditClick = {
                                                navigationState.navigateTo(Screen.ProductEdit(userData, product))
                                            },
                                            onDeleteClick = {
                                                showDeleteDialog = product
                                            }
                                        )
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }

                // Menús de ordenamiento y categorías
                if (showSortMenu) {
                    SortMenu(
                        currentSortOrder = uiState.sortOrder,
                        onSortOrderSelected = {
                            viewModel.setSortOrder(it)
                            showSortMenu = false
                        },
                        onDismiss = { showSortMenu = false }
                    )
                }

                if (showCategoryMenu) {
                    CategoryMenu(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = {
                            viewModel.filterByCategory(it)
                            showCategoryMenu = false
                        },
                        onDismiss = { showCategoryMenu = false }
                    )
                }

                if (showDeleteDialog != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = null },
                        title = { Text("Delete Product") },
                        text = {
                            Text("Are you sure you want to delete the product '${showDeleteDialog?.name}'? This action cannot be undone.")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val productToDelete = showDeleteDialog
                                    showDeleteDialog = null
                                    productToDelete?.id?.let { viewModel.deleteProduct(it) }
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
private fun SortMenu(
    currentSortOrder: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(250.dp)
    ) {
        Text(
            text = "Sort by",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Divider()

        SortOrderItem(
            text = "Name (A-Z)",
            selected = currentSortOrder == SortOrder.NAME_ASC,
            onClick = { onSortOrderSelected(SortOrder.NAME_ASC) }
        )

        SortOrderItem(
            text = "Name (Z-A)",
            selected = currentSortOrder == SortOrder.NAME_DESC,
            onClick = { onSortOrderSelected(SortOrder.NAME_DESC) }
        )

        SortOrderItem(
            text = "Price (low to high)",
            selected = currentSortOrder == SortOrder.PRICE_ASC,
            onClick = { onSortOrderSelected(SortOrder.PRICE_ASC) }
        )

        SortOrderItem(
            text = "Price (high to low)",
            selected = currentSortOrder == SortOrder.PRICE_DESC,
            onClick = { onSortOrderSelected(SortOrder.PRICE_DESC) }
        )

        SortOrderItem(
            text = "Stock (low to high)",
            selected = currentSortOrder == SortOrder.STOCK_ASC,
            onClick = { onSortOrderSelected(SortOrder.STOCK_ASC) }
        )

        SortOrderItem(
            text = "Stock (high to low)",
            selected = currentSortOrder == SortOrder.STOCK_DESC,
            onClick = { onSortOrderSelected(SortOrder.STOCK_DESC) }
        )
    }
}

@Composable
private fun SortOrderItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected,
                onClick = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text)
        }
    }
}

@Composable
private fun CategoryMenu(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(250.dp)
    ) {
        Text(
            text = "Filter by Category",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Divider()

        DropdownMenuItem(
            onClick = { onCategorySelected(null) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedCategory == null,
                    onClick = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("All categories")
            }
        }

        categories.forEach { category ->
            DropdownMenuItem(
                onClick = { onCategorySelected(category) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCategory == category,
                        onClick = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(category)
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductDto,
    onProductClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID
        Text(
            text = product.id.toString(),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.width(40.dp)
        )

        // Product Name and Brand
        Column(
            modifier = Modifier
                .weight(3f)
                .clickable(onClick = onProductClick)
        ) {
            Row(
                modifier = Modifier.weight(3f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder para la imagen del producto
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.name.take(1).uppercase(),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Category
        Text(
            text = product.category.name,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Price
        Text(
            text = "$${String.format("%.2f", product.price)}",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        // Inventory
        Text(
            text = if (product.variants?.isNotEmpty() == true)
                "${product.inventory} (${product.variants.size} variants)"
            else
                product.inventory.toString(),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        // Status
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val statusColor = when (product.status) {
                "IN_STOCK" -> Color(0xFF4CAF50) // Green
                "OUT_OF_STOCK" -> Color(0xFFF44336) // Red
                "LOW_STOCK" -> Color(0xFFFF9800) // Orange
                else -> MaterialTheme.colors.onSurface
            }

            Box(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (product.status) {
                        "IN_STOCK" -> "In Stock"
                        "OUT_OF_STOCK" -> "Out of Stock"
                        "LOW_STOCK" -> "Low Stock"
                        else -> product.status
                    },
                    style = MaterialTheme.typography.caption,
                    color = statusColor
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onProductClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "View",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colors.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
