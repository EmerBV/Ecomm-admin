package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import com.emerbv.ecommadmin.features.products.domain.AddProductVariantUseCase
import com.emerbv.ecommadmin.features.products.domain.DeleteProductVariantUseCase
import com.emerbv.ecommadmin.features.products.domain.GetProductByIdUseCase
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
        _uiState.update {
            it.copy(
                product = product,
                variants = product.variants ?: emptyList(),
                isLoading = false,
                errorMessage = null,
                successMessage = null
            )
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
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                product = result.data,
                                variants = result.data.variants ?: emptyList(),
                                errorMessage = null
                            )
                        }
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
                        refreshProductData(productId)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                successMessage = "Variant deleted successfully"
                            )
                        }
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