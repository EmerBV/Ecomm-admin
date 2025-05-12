package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.features.products.data.model.ProductDto

data class ProductListUiState(
    val products: List<ProductDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val searchQuery: String = "",
    val filteredProducts: List<ProductDto> = emptyList(),
    val sortOrder: SortOrder = SortOrder.NAME_ASC
)

enum class SortOrder {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    STOCK_ASC,
    STOCK_DESC
}
