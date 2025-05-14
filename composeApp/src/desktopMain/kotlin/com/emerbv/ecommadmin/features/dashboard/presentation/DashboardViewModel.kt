package com.emerbv.ecommadmin.features.dashboard.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.domain.GetAllCategoriesUseCase
import com.emerbv.ecommadmin.features.dashboard.presentation.components.LowInventoryUi
import com.emerbv.ecommadmin.features.dashboard.presentation.components.ProductPopularUi
import com.emerbv.ecommadmin.features.products.domain.GetAllProductsUseCase
import com.emerbv.ecommadmin.features.products.domain.GetProductsByStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalProducts: Int = 0,
    val totalCategories: Int = 0,
    val totalInventory: Int = 0,
    val preOrderCount: Int = 0,
    val inStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val popularProducts: List<ProductPopularUi> = emptyList(),
    val lowInventoryAlerts: List<LowInventoryUi> = emptyList()
)

class DashboardViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getProductsByStatusUseCase: GetProductsByStatusUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load products
            getAllProductsUseCase().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val products = result.data
                        val totalInventory = products.sumOf { it.inventory }
                        val preOrderProducts = products.filter { it.preOrder }

                        // Create popular products list based on salesCount
                        val popularProducts = products
                            .sortedByDescending { it.salesCount }
                            .take(5)
                            .map { product ->
                                ProductPopularUi(
                                    name = product.name,
                                    studio = product.brand,
                                    price = "$${product.price}",
                                    sold = product.salesCount
                                )
                            }

                        // Create low inventory alerts
                        val lowInventoryAlerts = products
                            .filter { it.inventory < 5 && it.inventory > 0 }
                            .take(5)
                            .map { product ->
                                LowInventoryUi(
                                    name = product.name,
                                    studio = product.brand,
                                    units = product.inventory
                                )
                            }

                        _uiState.update { currentState ->
                            currentState.copy(
                                totalProducts = products.size,
                                totalInventory = totalInventory,
                                preOrderCount = preOrderProducts.size,
                                popularProducts = popularProducts,
                                lowInventoryAlerts = lowInventoryAlerts
                            )
                        }

                        // Load product status counts
                        loadProductStatusCounts()
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load products: ${result.message}"
                        )}
                    }
                    is ApiResult.Loading -> {
                        // Already set isLoading to true above
                    }
                }
            }

            // Load categories
            getAllCategoriesUseCase().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update { it.copy(
                            totalCategories = result.data.size
                        )}
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(
                            errorMessage = "Failed to load categories: ${result.message}"
                        )}
                    }
                    is ApiResult.Loading -> {
                        // Already set isLoading to true above
                    }
                }
            }
        }
    }

    private fun loadProductStatusCounts() {
        scope.launch {
            // Load in-stock products
            getProductsByStatusUseCase("IN_STOCK").collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update { it.copy(
                            inStockCount = result.data.size,
                            isLoading = false
                        )}
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false
                        )}
                    }
                    is ApiResult.Loading -> {
                        // Already handled
                    }
                }
            }

            // Load out-of-stock products
            getProductsByStatusUseCase("OUT_OF_STOCK").collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update { it.copy(
                            outOfStockCount = result.data.size,
                            isLoading = false
                        )}
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false
                        )}
                    }
                    is ApiResult.Loading -> {
                        // Already handled
                    }
                }
            }
        }
    }
}