package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto

@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel,
    onProductSelected: (ProductDto) -> Unit,
    onAddProductClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Always refresh data when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadAllProducts()
    }

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
                    title = "Dashboard",
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onRefresh = { viewModel.refreshData() },
                    onSortClick = { showSortMenu = true },
                    onCategoryClick = { showCategoryMenu = true },
                    sortOrder = uiState.sortOrder,
                    selectedCategory = uiState.selectedCategory,
                    onBackClick = onBackClick
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Show main loading indicator
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Products",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage your product catalog",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Add Product button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    Button(
                        onClick = onAddProductClick,
                        modifier = Modifier.align(Alignment.TopEnd),
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

                // Table header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .shadow(1.dp)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
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

                // Display content based on state
                Box(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading && uiState.products.isEmpty()) {
                        // Initial loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.errorMessage != null && uiState.products.isEmpty()) {
                        // Error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
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
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
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
                    } else {
                        // List of products or empty state
                        if (uiState.filteredProducts.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No products found",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = uiState.filteredProducts,
                                    key = { it.id }
                                ) { product ->
                                    ProductRow(
                                        product = product,
                                        onProductSelected = onProductSelected
                                    )
                                    Divider()
                                }
                            }
                        }
                    }

                    if (uiState.isLoading && uiState.products.isNotEmpty()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                        )
                    }
                }
            }

            // Menus (sort and category filters)
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Divider()

                SortOrderItem(
                    text = "Name (A-Z)",
                    selected = uiState.sortOrder == SortOrder.NAME_ASC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.NAME_ASC)
                        showSortMenu = false
                    }
                )

                SortOrderItem(
                    text = "Name (Z-A)",
                    selected = uiState.sortOrder == SortOrder.NAME_DESC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.NAME_DESC)
                        showSortMenu = false
                    }
                )

                SortOrderItem(
                    text = "Price (low to high)",
                    selected = uiState.sortOrder == SortOrder.PRICE_ASC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.PRICE_ASC)
                        showSortMenu = false
                    }
                )

                SortOrderItem(
                    text = "Price (high to low)",
                    selected = uiState.sortOrder == SortOrder.PRICE_DESC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.PRICE_DESC)
                        showSortMenu = false
                    }
                )

                SortOrderItem(
                    text = "Stock (low to high)",
                    selected = uiState.sortOrder == SortOrder.STOCK_ASC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.STOCK_ASC)
                        showSortMenu = false
                    }
                )

                SortOrderItem(
                    text = "Stock (high to low)",
                    selected = uiState.sortOrder == SortOrder.STOCK_DESC,
                    onClick = {
                        viewModel.setSortOrder(SortOrder.STOCK_DESC)
                        showSortMenu = false
                    }
                )
            }

            DropdownMenu(
                expanded = showCategoryMenu,
                onDismissRequest = { showCategoryMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Divider()

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

                        Text("All categories")
                    }
                }

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

@Composable
private fun ProductsTopAppBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onSortClick: () -> Unit,
    onCategoryClick: () -> Unit,
    sortOrder: SortOrder,
    selectedCategory: String?,
    onBackClick: () -> Unit
) {
    var showSearch by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (showSearch) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search products...") },
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
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        },
        actions = {
            if (!showSearch) {
                IconButton(onClick = { showSearch = true }) {
                    Icon(Icons.Default.Search, "Search")
                }
            }

            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, "Refresh")
            }

            IconButton(onClick = onCategoryClick) {
                Icon(Icons.Default.Category, "Filter by category")
            }

            IconButton(onClick = onSortClick) {
                Icon(Icons.Default.Sort, "Sort")
            }
        }
    )
}

@Composable
private fun ProductRow(
    product: ProductDto,
    onProductSelected: (ProductDto) -> Unit
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

        // Product Name and Studio
        Column(
            modifier = Modifier.weight(3f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder para la imagen del producto
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
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
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = product.brand,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Category
        Text(
            text = product.category.name,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1.5f)
        )

        // Price
        Text(
            text = "$${product.price}",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        // Inventory
        Text(
            text = if (product.variants != null && product.variants.isNotEmpty())
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
                onClick = { onProductSelected(product) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "View",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { /* Implementar eliminación */ },
                modifier = Modifier.size(28.dp)
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