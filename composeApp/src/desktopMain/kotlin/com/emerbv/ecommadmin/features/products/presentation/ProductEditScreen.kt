package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.presentation.ProductEditViewModel

@Composable
fun ProductEditScreen(
    product: ProductDto,
    viewModel: ProductEditViewModel,
    onSaveClick: (ProductDto) -> Unit,
    onCancelClick: () -> Unit
) {
    val hasVariants = !product.variants.isNullOrEmpty()

    var name by remember { mutableStateOf(product.name) }
    var brand by remember { mutableStateOf(product.brand) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var inventory by remember { mutableStateOf(product.inventory.toString()) }
    var selectedCategoryId by remember { mutableStateOf(product.category.id) }
    var description by remember { mutableStateOf(product.description) }
    var discount by remember { mutableStateOf(product.discountPercentage.toString()) }
    var preOrder by remember { mutableStateOf(product.preOrder) }

    // Obtener las categorías al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    // Observar el estado de las categorías
    val categoryState by viewModel.categoryState.collectAsState()

    val scrollState = rememberScrollState()

    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Product Detail") },
                    navigationIcon = {
                        IconButton(onClick = onCancelClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basic Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Basic Information",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )

                        // Product Name field
                        Column {
                            Text(
                                text = "Product Name *",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary,
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Brand field
                        Column {
                            Text(
                                text = "Brand *",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )

                            OutlinedTextField(
                                value = brand,
                                onValueChange = { brand = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary,
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Price & Inventory in Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Price Field
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Price",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )

                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    enabled = !hasVariants,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colors.primary,
                                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                                        disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                    )
                                )

                                if (hasVariants) {
                                    Text(
                                        text = "Precio de la primera variante",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            // Inventory Field
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Inventory",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )

                                OutlinedTextField(
                                    value = inventory,
                                    onValueChange = { inventory = it },
                                    enabled = !hasVariants,
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colors.primary,
                                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                                        disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                        disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                    )
                                )

                                if (hasVariants) {
                                    Text(
                                        text = "Suma total del inventario de todas las variantes",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        // Category Dropdown
                        Column {
                            Text(
                                text = "Category",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )

                            var expanded by remember { mutableStateOf(false) }

                            // Mostrar el nombre de la categoría seleccionada
                            val selectedCategoryName = categoryState.categories.find { it.id == selectedCategoryId }?.name
                                ?: product.category.name

                            OutlinedTextField(
                                value = selectedCategoryName,
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown"
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary,
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                            )

                            // Mostrar carga o error si corresponde
                            when {
                                categoryState.isLoading -> {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    )
                                }
                                categoryState.errorMessage != null -> {
                                    Text(
                                        text = categoryState.errorMessage ?: "Error loading categories",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.error,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                categoryState.categories.forEach { category ->
                                    DropdownMenuItem(onClick = {
                                        selectedCategoryId = category.id
                                        expanded = false
                                    }) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = category.id == selectedCategoryId,
                                                onClick = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(category.name)
                                        }
                                    }
                                }
                            }
                        }

                        // Discount & Pre-Order Fields in Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Discount Field
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Discount (%)",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )

                                OutlinedTextField(
                                    value = discount,
                                    onValueChange = { discount = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colors.primary,
                                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                    )
                                )
                            }

                            // Pre-Order Field
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Pre-Order",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                )

                                // Switch Row for Pre-Order
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = preOrder,
                                        onCheckedChange = { preOrder = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colors.primary,
                                            checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = if (preOrder) "Yes" else "No",
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }
                        }

                        // Description Field
                        Column {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                maxLines = 5,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary,
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface
                        ),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(text = "Cancelar")
                    }

                    Button(
                        onClick = {
                            // Encontrar la categoría seleccionada
                            val selectedCategory = categoryState.categories.find { it.id == selectedCategoryId }
                                ?: product.category

                            // Create updated product object
                            val updatedProduct = product.copy(
                                name = name,
                                brand = brand,
                                price = price.toDoubleOrNull() ?: product.price,
                                inventory = inventory.toIntOrNull() ?: product.inventory,
                                category = selectedCategory,
                                description = description,
                                discountPercentage = discount.toIntOrNull() ?: product.discountPercentage,
                                preOrder = preOrder
                            )
                            onSaveClick(updatedProduct)
                        },
                        enabled = name.isNotBlank() && brand.isNotBlank() && !categoryState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Actualizar Producto")
                    }
                }
            }
        }
    }
}