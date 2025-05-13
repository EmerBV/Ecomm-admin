package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.domain.GetAllCategoriesUseCase
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
 * ViewModel para la pantalla de edición de productos
 */
class ProductEditViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // Estado para las categorías
    private val _categoryState = MutableStateFlow(CategoryUiState(isLoading = true))
    val categoryState: StateFlow<CategoryUiState> = _categoryState.asStateFlow()

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
}