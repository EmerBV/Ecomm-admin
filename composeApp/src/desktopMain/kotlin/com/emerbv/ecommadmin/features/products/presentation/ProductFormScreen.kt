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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto

@Composable
fun ProductFormScreen(
    isNewProduct: Boolean,
    initialProduct: ProductDto?,
    viewModel: ProductFormViewModel,
    onSaveClick: (ProductDto) -> Unit,
    onCancelClick: () -> Unit
) {
    val categoryState by viewModel.categoryState.collectAsState()
    val productState by viewModel.productState.collectAsState()

    // Inicializar el ViewModel para crear un nuevo producto
    LaunchedEffect(initialProduct) {
        if (isNewProduct) {
            viewModel.initForAdd()
        } else if (initialProduct != null) {
            viewModel.initForEdit(initialProduct)
        }
        viewModel.loadCategories()
    }

    // Redireccionar cuando se completa la operación
    LaunchedEffect(productState.isSuccess) {
        if (productState.isSuccess) {
            productState.product?.let { product ->
                onSaveClick(product)
            }
        }
    }

    val currentProduct = productState.product

    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(if (isNewProduct) "Add New Product" else "Edit Product")
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancelClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            }
        ) { paddingValues ->
            if (currentProduct == null) {
                // Mostrar carga inicial
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Mostrar formulario con el mismo estilo que ProductEditScreen
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Basic Information Card - Igual que en ProductEditScreen
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
                                    value = currentProduct.name,
                                    onValueChange = {
                                        viewModel.updateProductField(name = it)
                                    },
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
                                    value = currentProduct.brand,
                                    onValueChange = {
                                        viewModel.updateProductField(brand = it)
                                    },
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
                                    val hasVariants = currentProduct.variants != null && currentProduct.variants!!.isNotEmpty()

                                    Text(
                                        text = "Price",
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )

                                    var priceText by remember { mutableStateOf(currentProduct.price.toString()) }
                                    var isPriceFieldTouched by remember { mutableStateOf(false) }

                                    OutlinedTextField(
                                        value = priceText,
                                        onValueChange = { input ->
                                            // Si el campo está vacío o solo contiene un punto decimal, establecemos un valor válido
                                            if (!hasVariants) {
                                                val validatedValue = when {
                                                    input.isEmpty() -> {
                                                        isPriceFieldTouched = true
                                                        "0" // Evitamos enviar cadenas vacías
                                                    }
                                                    input == "." -> {
                                                        isPriceFieldTouched = true
                                                        "0."
                                                    }
                                                    else -> {
                                                        isPriceFieldTouched = true
                                                        // Filtramos caracteres no numéricos excepto el punto decimal
                                                        val filtered = input.filter { char -> char.isDigit() || char == '.' }
                                                        if (filtered.count { char -> char == '.' } > 1) {
                                                            val firstDecimalIndex = filtered.indexOf('.')
                                                            filtered.substring(0, firstDecimalIndex + 1) + filtered.substring(firstDecimalIndex + 1).replace(".", "")
                                                        } else {
                                                            filtered
                                                        }
                                                    }
                                                }

                                                priceText = validatedValue

                                                // Solo actualizamos el modelo cuando el valor es válido
                                                validatedValue.toDoubleOrNull()?.let { doubleValue ->
                                                    viewModel.updateProductField(price = doubleValue)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { focused ->
                                                if (focused.isFocused && !isPriceFieldTouched && priceText == "0.0") {
                                                    priceText = ""
                                                    isPriceFieldTouched = true
                                                }
                                            },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = MaterialTheme.colors.primary,
                                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                                            disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                            disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                        ),
                                        readOnly = hasVariants,
                                        enabled = !hasVariants
                                    )

                                    if (hasVariants) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Price is managed through variants",
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.secondary
                                        )
                                    }
                                }

                                // Inventory Field
                                Column(modifier = Modifier.weight(1f)) {
                                    val hasVariants = currentProduct.variants != null && currentProduct.variants!!.isNotEmpty()

                                    Text(
                                        text = "Inventory",
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )

                                    var inventoryText by remember { mutableStateOf(currentProduct.inventory.toString()) }
                                    var isInventoryFieldTouched by remember { mutableStateOf(false) }

                                    OutlinedTextField(
                                        value = inventoryText,
                                        onValueChange = { input ->
                                            // Solo permitir cambios si no hay variantes
                                            if (!hasVariants) {
                                                val validatedValue = when {
                                                    input.isEmpty() -> {
                                                        isInventoryFieldTouched = true
                                                        "0"
                                                    }
                                                    else -> {
                                                        isInventoryFieldTouched = true
                                                        // Solo permitimos dígitos para el inventario
                                                        input.filter { char -> char.isDigit() }
                                                    }
                                                }

                                                inventoryText = validatedValue

                                                validatedValue.toIntOrNull()?.let { intValue ->
                                                    viewModel.updateProductField(inventory = intValue)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { focused ->
                                                if (focused.isFocused && !isInventoryFieldTouched && inventoryText == "0" && !hasVariants) {
                                                    inventoryText = ""
                                                    isInventoryFieldTouched = true
                                                }
                                            },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = MaterialTheme.colors.primary,
                                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                                            disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                            disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                                        ),
                                        readOnly = hasVariants,
                                        enabled = !hasVariants
                                    )

                                    if (hasVariants) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Inventory is managed through variants",
                                            style = MaterialTheme.typography.caption,
                                            color = MaterialTheme.colors.secondary
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

                                OutlinedTextField(
                                    value = currentProduct.category.name,
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
                                            viewModel.updateProductField(categoryId = category.id)
                                            expanded = false
                                        }) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = category.id == currentProduct.category.id,
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

                                    var discountText by remember { mutableStateOf(currentProduct.discountPercentage.toString()) }
                                    var isDiscountFieldTouched by remember { mutableStateOf(false) }

                                    OutlinedTextField(
                                        value = discountText,
                                        onValueChange = { input ->
                                            val validatedValue = when {
                                                input.isEmpty() -> {
                                                    isDiscountFieldTouched = true
                                                    "0"
                                                }
                                                else -> {
                                                    isDiscountFieldTouched = true
                                                    // Solo permitimos dígitos y limitamos el valor a 100
                                                    val filtered = input.filter { char -> char.isDigit() }
                                                    filtered.toIntOrNull()?.let { num ->
                                                        if (num > 100) "100" else filtered
                                                    } ?: filtered
                                                }
                                            }

                                            discountText = validatedValue

                                            validatedValue.toIntOrNull()?.let { intValue ->
                                                viewModel.updateProductField(discountPercentage = intValue)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { focused ->
                                                if (focused.isFocused && !isDiscountFieldTouched && discountText == "0") {
                                                    discountText = ""
                                                    isDiscountFieldTouched = true
                                                }
                                            },
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
                                            checked = currentProduct.preOrder,
                                            onCheckedChange = {
                                                viewModel.updateProductField(preOrder = it)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = MaterialTheme.colors.primary,
                                                checkedTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = if (currentProduct.preOrder) "Yes" else "No",
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
                                    value = currentProduct.description,
                                    onValueChange = {
                                        viewModel.updateProductField(description = it)
                                    },
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

                    // Sección de imágenes
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Product Images",
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Add Images",
                                        tint = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Images can be added after creating the product",
                                        style = MaterialTheme.typography.caption,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    // Mensaje de error si existe
                    if (productState.errorMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                            elevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colors.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = productState.errorMessage ?: "",
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.body2
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
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = { viewModel.saveProduct() },
                            enabled = viewModel.isFormValid() && !productState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary
                            )
                        ) {
                            if (productState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (isNewProduct) "Create Product" else "Update Product")
                        }
                    }
                }
            }
        }
    }
}

