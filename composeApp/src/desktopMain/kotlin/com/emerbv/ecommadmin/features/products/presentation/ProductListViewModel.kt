package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.domain.DeleteProductUseCase
import com.emerbv.ecommadmin.features.products.domain.GetAllProductsUseCase
import com.emerbv.ecommadmin.features.products.domain.GetProductsByCategoryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private val tokenManager: TokenManager
) {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadAllProducts()
    }

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    fun loadAllProducts() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getAllProductsUseCase().collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is ApiResult.Success -> {
                        val products = result.data
                        val categories = extractUniqueCategories(products)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = null,
                                products = products,
                                filteredProducts = products,
                                categories = categories
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteProduct(productId: Long) {
        if (!tokenManager.isLoggedIn()) {
            _uiState.update { it.copy(
                errorMessage = "You must be logged in to delete products",
                isLoading = false
            )}
            return
        }

        // Imprimir estado del token para depuración
        println("Attempting to delete product $productId. Token: ${tokenManager.getToken()?.take(10)}...")

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            deleteProductUseCase(productId).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _successMessage.value = "Product deleted successfully"

                        // Eliminar el producto de la lista local
                        _uiState.update { currentState ->
                            val updatedProducts = currentState.products.filter { it.id != productId }
                            val updatedFilteredProducts = currentState.filteredProducts.filter { it.id != productId }

                            currentState.copy(
                                products = updatedProducts,
                                filteredProducts = updatedFilteredProducts,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                        // Resetear el mensaje de éxito después de mostrarlo
                        delay(3000)
                        _successMessage.value = null
                    }
                    is ApiResult.Error -> {
                        val errorMsg = when (result.code) {
                            403 -> "You don't have permission to delete products. Admin privileges required."
                            401 -> "Your session has expired. Please login again."
                            else -> "Error deleting product: ${result.message}"
                        }

                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )}
                    }
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
        _successMessage.value = null
    }

    fun filterByCategory(category: String?) {
        if (category == null) {
            // Si la categoría es nula, mostrar todos los productos
            _uiState.update {
                it.copy(
                    selectedCategory = null,
                    filteredProducts = it.products,
                    errorMessage = null
                )
            }
            return
        }

        _uiState.update { it.copy(selectedCategory = category) }

        scope.launch {
            getProductsByCategoryUseCase(category).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = null,
                                filteredProducts = result.data
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message,
                                filteredProducts = emptyList()
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterProducts()
    }

    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        sortProducts()
    }

    private fun filterProducts() {
        val query = _uiState.value.searchQuery.lowercase()
        val allProducts = _uiState.value.products
        val category = _uiState.value.selectedCategory

        if (query.isBlank() && category == null) {
            _uiState.update { it.copy(filteredProducts = allProducts) }
            return
        }

        val filtered = allProducts.filter { product ->
            val matchesQuery = query.isBlank() ||
                    product.name.lowercase().contains(query) ||
                    product.brand.lowercase().contains(query) ||
                    product.description.lowercase().contains(query)

            val matchesCategory = category == null || product.category.name == category

            matchesQuery && matchesCategory
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
        sortProducts()
    }

    private fun sortProducts() {
        val filtered = _uiState.value.filteredProducts.toMutableList()

        when (_uiState.value.sortOrder) {
            SortOrder.NAME_ASC -> filtered.sortBy { it.name }
            SortOrder.NAME_DESC -> filtered.sortByDescending { it.name }
            SortOrder.PRICE_ASC -> filtered.sortBy { it.price }
            SortOrder.PRICE_DESC -> filtered.sortByDescending { it.price }
            SortOrder.STOCK_ASC -> filtered.sortBy { it.inventory }
            SortOrder.STOCK_DESC -> filtered.sortByDescending { it.inventory }
        }

        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    private fun extractUniqueCategories(products: List<ProductDto>): List<String> {
        return products.mapNotNull { it.category.name }.distinct().sorted()
    }

    fun refreshData() {
        loadAllProducts()
    }
}