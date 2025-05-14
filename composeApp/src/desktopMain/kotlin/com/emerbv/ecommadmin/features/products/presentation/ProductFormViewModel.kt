package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.domain.GetAllCategoriesUseCase
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.domain.AddProductUseCase
import com.emerbv.ecommadmin.features.products.domain.UpdateProductUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado para manejar las categorías en la pantalla de edición de productos
 */
data class CategoryUiState(
    val categories: List<CategoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Estado para manejar el producto en edición/creación
 */
data class ProductFormState(
    val product: ProductDto? = null,
    val isNew: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel para la pantalla de edición de productos
 */
class ProductFormViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // Estado para las categorías
    private val _categoryState = MutableStateFlow(CategoryUiState(isLoading = true))
    val categoryState: StateFlow<CategoryUiState> = _categoryState.asStateFlow()

    // Estado para el producto en edición/creación
    private val _productState = MutableStateFlow(ProductFormState())
    val productState: StateFlow<ProductFormState> = _productState.asStateFlow()

    /**
     * Inicializa el formulario para editar un producto existente
     */
    fun initForEdit(product: ProductDto) {
        _productState.update {
            it.copy(
                product = product,
                isNew = false,
                isLoading = false,
                isSuccess = false,
                errorMessage = null
            )
        }
    }

    /**
     * Inicializa el formulario para añadir un nuevo producto
     */
    fun initForAdd() {
        // Crear un producto vacío con valores predeterminados
        _productState.update {
            it.copy(
                product = null,
                isNew = true,
                isLoading = false,
                isSuccess = false,
                errorMessage = null
            )
        }
    }

    fun loadCategories() {
        scope.launch {
            try {
                _categoryState.update { it.copy(isLoading = true, errorMessage = null) }

                // Realizamos la llamada al API a través del caso de uso
                getAllCategoriesUseCase().collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            _categoryState.update { it.copy(isLoading = true) }
                        }

                        is ApiResult.Success -> {
                            val categories = result.data

                            _categoryState.update {
                                it.copy(
                                    categories = categories,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }

                            // Si estamos creando un nuevo producto y hay categorías, seleccionar la primera por defecto
                            if (_productState.value.isNew && _productState.value.product == null && categories.isNotEmpty()) {
                                createEmptyProduct(categories.first())
                            }
                        }

                        is ApiResult.Error -> {
                            _categoryState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _categoryState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error cargando categorías: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Crea un producto vacío con valores predeterminados y la categoría especificada
     */
    private fun createEmptyProduct(defaultCategory: CategoryDto) {
        val emptyProduct = ProductDto(
            id = 0, // El backend asignará un ID real
            name = "",
            brand = "",
            price = 0.0,
            inventory = 0,
            description = "",
            category = defaultCategory,
            discountPercentage = 0,
            status = "IN_STOCK",
            preOrder = false
        )

        _productState.update { it.copy(product = emptyProduct) }
    }

    /**
     * Actualiza el producto con los nuevos valores
     */
    fun updateProductField(
        name: String? = null,
        brand: String? = null,
        price: Double? = null,
        inventory: Int? = null,
        description: String? = null,
        categoryId: Long? = null,
        discountPercentage: Int? = null,
        status: String? = null,
        preOrder: Boolean? = null
    ) {
        val currentProduct = _productState.value.product ?: return

        // Buscar la categoría por ID si se proporciona un nuevo ID de categoría
        val newCategory = if (categoryId != null) {
            _categoryState.value.categories.find { it.id == categoryId } ?: currentProduct.category
        } else {
            currentProduct.category
        }

        // Determinar el nuevo valor de inventario
        val newInventory = inventory ?: currentProduct.inventory

        // Definir el estado automáticamente basado en el inventario, a menos que
        // se proporcione explícitamente un valor para status
        val newStatus = if (status != null) {
            status
        } else if (newInventory <= 0) {
            "OUT_OF_STOCK"
        } else {
            "IN_STOCK"
        }

        val updatedProduct = currentProduct.copy(
            name = name ?: currentProduct.name,
            brand = brand ?: currentProduct.brand,
            price = price ?: currentProduct.price,
            inventory = inventory ?: currentProduct.inventory,
            description = description ?: currentProduct.description,
            category = newCategory,
            discountPercentage = discountPercentage ?: currentProduct.discountPercentage,
            status = newStatus,
            preOrder = preOrder ?: currentProduct.preOrder
        )

        _productState.update { it.copy(product = updatedProduct) }
    }

    /**
     * Guarda el producto (crea uno nuevo o actualiza uno existente)
     */
    fun saveProduct() {
        val productToSave = _productState.value.product ?: return

        // Asegurarse de que el estado es correcto basado en el inventario total
        val finalProduct = if (productToSave.variants?.isNotEmpty() == true) {
            // Si tiene variantes, calcular el inventario total de las variantes
            val totalInventory = productToSave.variants.sumOf { it.inventory }
            val correctStatus = if (totalInventory <= 0) "OUT_OF_STOCK" else "IN_STOCK"
            productToSave.copy(status = correctStatus)
        } else {
            // Si no tiene variantes, usar el inventario del producto
            val correctStatus = if (productToSave.inventory <= 0) "OUT_OF_STOCK" else "IN_STOCK"
            productToSave.copy(status = correctStatus)
        }

        val isNew = _productState.value.isNew

        _productState.update { it.copy(isLoading = true, errorMessage = null) }

        scope.launch {
            try {
                val result = if (isNew) {
                    addProductUseCase(finalProduct)
                } else {
                    updateProductUseCase(finalProduct)
                }

                result.collect { apiResult ->
                    when (apiResult) {
                        is ApiResult.Loading -> {
                            _productState.update { it.copy(isLoading = true) }
                        }
                        is ApiResult.Success -> {
                            _productState.update {
                                it.copy(
                                    product = apiResult.data,
                                    isNew = false,
                                    isLoading = false,
                                    isSuccess = true,
                                    errorMessage = null
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _productState.update {
                                it.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    errorMessage = apiResult.message
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _productState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Valida si el formulario es válido para guardar
     */
    fun isFormValid(): Boolean {
        val product = _productState.value.product ?: return false
        return product.name.isNotBlank() && product.brand.isNotBlank()
    }

}