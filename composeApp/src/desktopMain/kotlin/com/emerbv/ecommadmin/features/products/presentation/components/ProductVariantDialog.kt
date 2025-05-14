package com.emerbv.ecommadmin.features.products.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emerbv.ecommadmin.features.products.data.model.VariantDto

/**
 * Estado para el formulario de variante
 */
data class VariantFormState(
    val id: Long = 0,
    val name: String = "",
    val price: String = "0.00",
    val inventory: String = "0",
    val isEdit: Boolean = false
)

/**
 * Diálogo para añadir o editar una variante de producto
 *
 * @param isVisible si el diálogo es visible
 * @param variant variante a editar, null si es una nueva variante
 * @param onDismiss callback cuando se cierra el diálogo
 * @param onSave callback cuando se guarda la variante
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProductVariantDialog(
    isVisible: Boolean,
    variant: VariantDto? = null,
    onDismiss: () -> Unit,
    onSave: (VariantFormState) -> Unit
) {
    if (!isVisible) return

    // Estado del formulario
    var formState by remember {
        mutableStateOf(
            if (variant != null) {
                VariantFormState(
                    id = variant.id,
                    name = variant.name,
                    price = variant.price.toString(),
                    inventory = variant.inventory.toString(),
                    isEdit = true
                )
            } else {
                VariantFormState()
            }
        )
    }

    // Validación
    val isNameValid = formState.name.isNotBlank()
    val isPriceValid = try {
        formState.price.toDouble() >= 0
    } catch (e: NumberFormatException) {
        false
    }
    val isInventoryValid = try {
        formState.inventory.toInt() >= 0
    } catch (e: NumberFormatException) {
        false
    }
    val isFormValid = isNameValid && isPriceValid && isInventoryValid

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título y botón cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (formState.isEdit) "Edit Product Variant" else "Add Product Variant",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Divider()

                // Campos del formulario
                // Campo Nombre Variante
                Column {
                    Text(
                        text = "Variant Name *",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )

                    OutlinedTextField(
                        value = formState.name,
                        onValueChange = { formState = formState.copy(name = it) },
                        placeholder = { Text("e.g., Size L, Color Blue, etc.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = formState.name.isNotBlank() && !isNameValid,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        )
                    )

                    if (formState.name.isNotBlank() && !isNameValid) {
                        Text(
                            text = "Variant name cannot be empty",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }

                // Campo Precio
                Column {
                    Text(
                        text = "Price *",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )

                    var isPriceFocused by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = formState.price,
                        onValueChange = { input ->
                            val validatedInput = input.replace(",", ".")
                                .filter { it.isDigit() || it == '.' }
                                .let { value ->
                                    // Asegurarse de que solo haya un punto decimal
                                    if (value.count { it == '.' } > 1) {
                                        val firstDecimal = value.indexOf('.')
                                        value.substring(0, firstDecimal + 1) + value.substring(firstDecimal + 1).replace(".", "")
                                    } else {
                                        value
                                    }
                                }

                            formState = formState.copy(price = validatedInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                isPriceFocused = it.isFocused
                                if (!it.isFocused && formState.price.isEmpty()) {
                                    formState = formState.copy(price = "0.00")
                                }
                                if (it.isFocused && formState.price == "0.00") {
                                    formState = formState.copy(price = "")
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = isPriceFocused && !isPriceValid,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        )
                    )

                    if (isPriceFocused && !isPriceValid) {
                        Text(
                            text = "Please enter a valid price",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }

                // Campo Inventario
                Column {
                    Text(
                        text = "Inventory *",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )

                    var isInventoryFocused by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = formState.inventory,
                        onValueChange = { input ->
                            val validatedInput = input.filter { it.isDigit() }
                            formState = formState.copy(inventory = validatedInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                isInventoryFocused = it.isFocused
                                if (!it.isFocused && formState.inventory.isEmpty()) {
                                    formState = formState.copy(inventory = "0")
                                }
                            },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = isInventoryFocused && !isInventoryValid,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                        )
                    )

                    if (isInventoryFocused && !isInventoryValid) {
                        Text(
                            text = "Please enter a valid inventory amount",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }

                Divider()

                // Acciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface
                        )
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onSave(formState) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        enabled = isFormValid
                    ) {
                        Icon(
                            imageVector = if (formState.isEdit) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (formState.isEdit) "Save" else "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (formState.isEdit) "Save Variant" else "Add Variant")
                    }
                }
            }
        }
    }
}