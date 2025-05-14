package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.presentation.components.ProductVariantDialog
import com.emerbv.ecommadmin.features.products.presentation.components.VariantFormState
import kotlinx.coroutines.flow.collectLatest
import org.koin.java.KoinJavaComponent.get

@Composable
fun ProductDetailScreen(
    product: ProductDto,
    variantsViewModel: ProductVariantsViewModel,
    onBackClick: () -> Unit,
    onEditClick: (ProductDto) -> Unit,
    onDeleteClick: (ProductDto) -> Unit
) {
    val variantsUiState by variantsViewModel.uiState.collectAsState()

    // Inicializar el ViewModel con los datos del producto
    LaunchedEffect(product) {
        variantsViewModel.initWithProduct(product)
    }

    // Monitorear mensajes de éxito para mostrar SnackBar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(variantsUiState.successMessage) {
        variantsUiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            variantsViewModel.clearMessages()
        }
    }

    // Estado para el diálogo de confirmación de eliminación
    var showDeleteDialog by remember { mutableStateOf<VariantDto?>(null) }

    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Products") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->

            // Mostrar diálogo de variante si está visible
            if (variantsUiState.isDialogVisible) {
                ProductVariantDialog(
                    isVisible = true,
                    variant = variantsUiState.currentVariant,
                    onDismiss = { variantsViewModel.hideDialog() },
                    onSave = { formState -> variantsViewModel.saveVariant(formState) }
                )
            }

            // Diálogo de confirmación para eliminar variante
            if (showDeleteDialog != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = { Text("Delete Variant") },
                    text = { Text("Are you sure you want to delete the variant '${showDeleteDialog?.name}'? This action cannot be undone.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog?.id?.let { variantsViewModel.deleteVariant(it) }
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Product Title, Studio, and Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product.brand,
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onEditClick(product) },
                                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = MaterialTheme.colors.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit")
                            }

                            Button(
                                onClick = { onDeleteClick(product) },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFD32F2F), // Red color
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }
                    }
                }

                // Main content with images and info
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Product Images Section
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            elevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Product Images",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (product.images != null && product.images.isNotEmpty()) {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        items(product.images) { image ->
                                            Box(
                                                modifier = Modifier
                                                    .size(120.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                // Placeholder for actual image
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = "Product Image",
                                                    modifier = Modifier.size(48.dp),
                                                    tint = MaterialTheme.colors.primary.copy(alpha = 0.5f)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = "No Images",
                                                modifier = Modifier.size(48.dp),
                                                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No images available",
                                                style = MaterialTheme.typography.caption,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedButton(
                                    onClick = { /* Implement file picker */ },
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                                ) {
                                    Text("Choose files")
                                }

                                Text(
                                    text = "No file selected",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(top = 4.dp)
                                )
                            }
                        }

                        // Basic Information Section
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            elevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Basic Information",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Price",
                                    value = "$${product.price}"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Inventory",
                                    value = "${product.inventory} units"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Category",
                                    value = product.category.name
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Discount",
                                    value = "${product.discountPercentage}%"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Status",
                                    value = "",
                                    valueContent = {
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
                                                style = MaterialTheme.typography.body2,
                                                color = statusColor
                                            )
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                ProductInfoRow(
                                    label = "Pre-Order",
                                    value = if (product.preOrder) "Yes" else "No"
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product.description.ifEmpty { "No description provided." },
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Variants Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Variants",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "Product variations and options",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )

                                OutlinedButton(
                                    onClick = {
                                        variantsViewModel.showAddVariantDialog()
                                    },
                                    border = BorderStroke(1.dp, MaterialTheme.colors.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Variant",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Variant")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Variant Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.background)
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "NAME",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "PRICE",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "INVENTORY",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "ACTIONS",
                                    style = MaterialTheme.typography.subtitle2,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Divider()

                            // Variant Table Rows
                            val currentVariants = variantsUiState.variants.ifEmpty { product.variants ?: emptyList() }

                            if (variantsUiState.isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (currentVariants.isNotEmpty()) {
                                Column {
                                    currentVariants.forEach { variant ->
                                        VariantRow(
                                            variant = variant,
                                            onEditClick = { variantsViewModel.showEditVariantDialog(variant) },
                                            onDeleteClick = { showDeleteDialog = variant }
                                        )
                                        Divider()
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No variants available for this product",
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Error message
                            if (variantsUiState.errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = variantsUiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.error,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductInfoRow(
    label: String,
    value: String,
    valueContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium
        )

        if (valueContent != null) {
            valueContent()
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun VariantRow(
    variant: VariantDto,
    onEditClick: (VariantDto) -> Unit,
    onDeleteClick: (VariantDto) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = variant.name,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$${variant.price}",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${variant.inventory} units",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { onEditClick(variant) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Variant",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { onDeleteClick(variant) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Variant",
                    tint = Color(0xFFD32F2F), // Red color
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}