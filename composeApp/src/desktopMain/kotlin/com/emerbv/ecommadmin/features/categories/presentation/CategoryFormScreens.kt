package com.emerbv.ecommadmin.features.categories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto

/**
 * Pantalla para añadir una nueva categoría
 */
@Composable
fun CategoryAddScreen(
    viewModel: CategoryFormViewModel,
    onSaveComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.resetForm()
    }

    CategoryForm(
        title = "Add Category",
        categoryName = uiState.categoryName,
        onCategoryNameChange = { viewModel.updateCategoryName(it) },
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onSaveClick = { viewModel.addCategory() },
        onCancelClick = onCancel
    )

    // Observar el éxito para volver a la lista
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveComplete()
        }
    }
}

/**
 * Pantalla para editar una categoría existente
 */
@Composable
fun CategoryEditScreen(
    category: CategoryDto,
    viewModel: CategoryFormViewModel,
    onSaveComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(category) {
        viewModel.initWithCategory(category)
    }

    CategoryForm(
        title = "Edit Category",
        categoryName = uiState.categoryName,
        onCategoryNameChange = { viewModel.updateCategoryName(it) },
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onSaveClick = { viewModel.updateCategory() },
        onCancelClick = onCancel
    )

    // Observar el éxito para volver a la lista
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSaveComplete()
        }
    }
}

/**
 * Formulario reutilizable para añadir o editar categorías
 */
@Composable
fun CategoryForm(
    title: String,
    categoryName: String,
    onCategoryNameChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .widthIn(max = 500.dp),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Título del formulario
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Campo para el nombre de la categoría
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Category Name",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = categoryName,
                                onValueChange = onCategoryNameChange,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("Enter category name") },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary,
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Opciones futuras para cargar imagen y configuración
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colors.primary.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = MaterialTheme.colors.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Image upload will be available in a future update",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Mensajes de error
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botones de acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = onCancelClick,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = MaterialTheme.colors.onSurface
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = onSaveClick,
                                enabled = categoryName.isNotBlank() && !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    contentColor = MaterialTheme.colors.onPrimary,
                                    disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.3f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colors.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}