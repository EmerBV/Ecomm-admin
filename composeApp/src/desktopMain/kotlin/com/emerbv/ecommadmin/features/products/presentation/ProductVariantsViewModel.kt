package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.domain.AddProductVariantUseCase
import com.emerbv.ecommadmin.features.products.domain.DeleteProductVariantUseCase
import com.emerbv.ecommadmin.features.products.domain.GetProductByIdUseCase
import com.emerbv.ecommadmin.features.products.domain.UpdateProductUseCase
import com.emerbv.ecommadmin.features.products.domain.UpdateProductVariantUseCase
import com.emerbv.ecommadmin.features.products.presentation.components.VariantFormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de UI para la gestión de variantes
 */
data class ProductVariantsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val product: ProductDto? = null,
    val variants: List<VariantDto> = emptyList(),
    val currentVariant: VariantDto? = null,
    val isDialogVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val successMessage: String? = null
)

/**
 * ViewModel para gestionar las variantes de un producto
 */
class ProductVariantsViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addProductVariantUseCase: AddProductVariantUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val updateProductVariantUseCase: UpdateProductVariantUseCase,
    private val deleteProductVariantUseCase: DeleteProductVariantUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(ProductVariantsUiState())
    val uiState: StateFlow<ProductVariantsUiState> = _uiState.asStateFlow()

    /**
     * Inicializa el ViewModel con los datos del producto
     */
    fun initWithProduct(product: ProductDto) {
        val variants = product.variants ?: emptyList()

        _uiState.update {
            it.copy(
                product = product,
                variants = variants,
                isLoading = false,
                errorMessage = null,
                successMessage = null
            )
        }

        // Si el producto tiene ID y no hay variantes, intentamos cargar las variantes desde la API
        if (product.id > 0 && variants.isEmpty()) {
            refreshProductData(product.id)
        }
    }

    /**
     * Recarga los datos del producto desde la API
     */
    fun refreshProductData(productId: Long) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getProductByIdUseCase(productId).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val updatedProduct = result.data
                        val updatedVariants = updatedProduct.variants ?: emptyList()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                product = updatedProduct,
                                variants = updatedVariants,
                                errorMessage = null
                            )
                        }

                        println("Producto actualizado: ${updatedProduct.name} con ${updatedVariants.size} variantes")
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error loading product: ${result.message}"
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    /**
     * Muestra el diálogo para añadir una nueva variante
     */
    fun showAddVariantDialog() {
        _uiState.update {
            it.copy(
                isDialogVisible = true,
                isEditMode = false,
                currentVariant = null,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    /**
     * Muestra el diálogo para editar una variante existente
     */
    fun showEditVariantDialog(variant: VariantDto) {
        _uiState.update {
            it.copy(
                isDialogVisible = true,
                isEditMode = true,
                currentVariant = variant,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    /**
     * Cierra el diálogo
     */
    fun hideDialog() {
        _uiState.update {
            it.copy(
                isDialogVisible = false,
                currentVariant = null,
                errorMessage = null
            )
        }
    }

    /**
     * Guarda una variante (nueva o editada)
     */
    fun saveVariant(formState: VariantFormState) {
        val productId = _uiState.value.product?.id ?: return

        try {
            val price = formState.price.toDoubleOrNull() ?: 0.0
            val inventory = formState.inventory.toIntOrNull() ?: 0

            val variantDto = VariantDto(
                id = formState.id,
                name = formState.name,
                price = price,
                inventory = inventory
            )

            scope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val result = if (formState.isEdit) {
                    updateProductVariantUseCase(productId, variantDto)
                } else {
                    addProductVariantUseCase(productId, variantDto)
                }

                result.collect { apiResult ->
                    when (apiResult) {
                        is ApiResult.Success -> {
                            refreshProductData(productId)
                            _uiState.update {
                                it.copy(
                                    isDialogVisible = false,
                                    successMessage = if (formState.isEdit)
                                        "Variant updated successfully"
                                    else
                                        "Variant added successfully"
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = apiResult.message
                                )
                            }
                        }
                        is ApiResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Error processing variant data: ${e.message}"
                )
            }
        }
    }

    /**
     * Elimina una variante
     */
    fun deleteVariant(variantId: Long) {
        val productId = _uiState.value.product?.id ?: return

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deleteProductVariantUseCase(productId, variantId).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        // Actualiza inmediatamente la lista de variantes en el estado UI
                        _uiState.update { currentState ->
                            currentState.copy(
                                variants = currentState.variants.filter { it.id != variantId },
                                isLoading = false,
                                successMessage = "Variant deleted successfully"
                            )
                        }

                        // Recarga los datos completos del producto después para sincronizar todo
                        refreshProductData(productId)
                        ensureCorrectInventoryAndStatus()
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error deleting variant: ${result.message}"
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun ensureCorrectInventoryAndStatus() {
        val currentProduct = _uiState.value.product ?: return
        val variants = _uiState.value.variants

        // Si no hay variantes, el inventario debe ser 0 y el estado OUT_OF_STOCK
        if (variants.isEmpty() && (currentProduct.inventory > 0 || currentProduct.status != "OUT_OF_STOCK")) {
            val updatedProduct = currentProduct.copy(
                inventory = 0,
                status = "OUT_OF_STOCK"
            )

            // Actualizar el producto en el backend
            scope.launch {
                updateProductUseCase(updatedProduct).collect { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            _uiState.update { state ->
                                state.copy(
                                    product = result.data,
                                    successMessage = "Product inventory updated"
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.update { state ->
                                state.copy(
                                    errorMessage = "Failed to update product inventory: ${result.message}"
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * Limpia mensajes de éxito o error
     */
    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }
}