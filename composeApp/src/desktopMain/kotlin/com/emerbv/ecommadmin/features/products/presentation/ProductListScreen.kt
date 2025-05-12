package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.presentation.components.EmptyProductList
import com.emerbv.ecommadmin.features.products.presentation.components.ProductListItem

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onProductSelected: (ProductDto) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.updateSearchQuery(searchQuery)
    }

    EcommAdminTheme {
        Scaffold(
            topBar = {
                ProductsTopAppBar(
                    title = "Productos",
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onRefresh = { viewModel.refreshData() },
                    onSortClick = { showSortMenu = true },
                    onCategoryClick = { showCategoryMenu = true },
                    sortOrder = uiState.sortOrder,
                    selectedCategory = uiState.selectedCategory
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading && uiState.products.isEmpty()) {
                    // Mostrar carga inicial
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (uiState.errorMessage != null && uiState.products.isEmpty()) {
                    // Mostrar error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colors.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Error al cargar productos",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.errorMessage ?: "Error desconocido",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.refreshData() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reintentar",
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Reintentar")
                        }
                    }
                } else {
                    // Mostrar lista de productos
                    if (uiState.filteredProducts.isEmpty()) {
                        EmptyProductList(
                            message = "No se encontraron productos${
                                if (uiState.searchQuery.isNotEmpty()) " para '${uiState.searchQuery}'" else ""
                            }${
                                if (uiState.selectedCategory != null) " en la categoría '${uiState.selectedCategory}'" else ""
                            }"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(
                                items = uiState.filteredProducts,
                                key = { it.id }
                            ) { product ->
                                ProductListItem(
                                    product = product,
                                    onClick = onProductSelected
                                )
                            }
                        }
                    }

                    // Mostrar indicador de carga para actualizaciones
                    AnimatedVisibility(
                        visible = uiState.isLoading && uiState.products.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Menú desplegable para ordenamiento
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = "Ordenar por",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Divider()

                    SortOrderItem(
                        text = "Nombre (A-Z)",
                        selected = uiState.sortOrder == SortOrder.NAME_ASC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.NAME_ASC)
                            showSortMenu = false
                        }
                    )

                    SortOrderItem(
                        text = "Nombre (Z-A)",
                        selected = uiState.sortOrder == SortOrder.NAME_DESC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.NAME_DESC)
                            showSortMenu = false
                        }
                    )

                    SortOrderItem(
                        text = "Precio (menor a mayor)",
                        selected = uiState.sortOrder == SortOrder.PRICE_ASC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.PRICE_ASC)
                            showSortMenu = false
                        }
                    )

                    SortOrderItem(
                        text = "Precio (mayor a menor)",
                        selected = uiState.sortOrder == SortOrder.PRICE_DESC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.PRICE_DESC)
                            showSortMenu = false
                        }
                    )

                    SortOrderItem(
                        text = "Stock (menor a mayor)",
                        selected = uiState.sortOrder == SortOrder.STOCK_ASC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.STOCK_ASC)
                            showSortMenu = false
                        }
                    )

                    SortOrderItem(
                        text = "Stock (mayor a menor)",
                        selected = uiState.sortOrder == SortOrder.STOCK_DESC,
                        onClick = {
                            viewModel.setSortOrder(SortOrder.STOCK_DESC)
                            showSortMenu = false
                        }
                    )
                }

                // Menú desplegable para categorías
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Divider()

                    // Opción para mostrar todas las categorías
                    DropdownMenuItem(
                        onClick = {
                            viewModel.filterByCategory(null)
                            showCategoryMenu = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.selectedCategory == null,
                                onClick = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Todas las categorías")
                        }
                    }

                    // Lista de categorías disponibles
                    uiState.categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.filterByCategory(category)
                                showCategoryMenu = false
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = uiState.selectedCategory == category,
                                    onClick = null
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(category)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductsTopAppBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onSortClick: () -> Unit,
    onCategoryClick: () -> Unit,
    sortOrder: SortOrder,
    selectedCategory: String?
) {
    var showSearch by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (showSearch) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar productos...") },
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        focusedIndicatorColor = MaterialTheme.colors.primary,
                        unfocusedIndicatorColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colors.primary
                    )
                )
            } else {
                Text(
                    text = if (selectedCategory != null) "$title - $selectedCategory" else title
                )
            }
        },
        navigationIcon = {
            if (showSearch) {
                IconButton(onClick = {
                    showSearch = false
                    onSearchQueryChange("")
                }) {
                    Icon(Icons.Default.ArrowBack, "Volver")
                }
            }
        },
        actions = {
            if (!showSearch) {
                IconButton(onClick = { showSearch = true }) {
                    Icon(Icons.Default.Search, "Buscar")
                }
            }

            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, "Actualizar")
            }

            IconButton(onClick = onCategoryClick) {
                Icon(Icons.Default.Category, "Filtrar por categoría")
            }

            IconButton(onClick = onSortClick) {
                Icon(Icons.Default.Sort, "Ordenar")
            }
        }
    )
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