package com.emerbv.ecommadmin.features.categories.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.domain.DeleteCategoryUseCase
import com.emerbv.ecommadmin.features.categories.domain.GetAllCategoriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryListUiState(
    val categories: List<CategoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class CategoryListViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // UI state para la pantalla de lista de categorías
    private val _uiState = MutableStateFlow(CategoryListUiState(isLoading = true))
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    // Carga todas las categorías
    fun loadCategories() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            println("Iniciando carga de categorías desde el ViewModel")

            getAllCategoriesUseCase().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val sortedCategories = result.data.sortedBy { category -> category.id }
                        println("Categorías cargadas correctamente: ${sortedCategories.size}")

                        _uiState.update { state ->
                            state.copy(
                                categories = sortedCategories,
                                isLoading = false,
                                errorMessage = ""
                            )
                        }

                        // Verificación adicional del estado actualizado
                        println("Estado actualizado: ${_uiState.value.categories.size} categorías en el estado")
                    }
                    is ApiResult.Error -> {
                        println("Error al cargar categorías: ${result.message}")
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is ApiResult.Loading -> {
                        _uiState.update { state -> state.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    // Elimina una categoría
    fun deleteCategory(categoryId: Long) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            deleteCategoryUseCase(categoryId).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                categories = currentState.categories.filter { it.id != categoryId },
                                isLoading = false,
                                successMessage = "Category deleted successfully!"
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
                    is ApiResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    // Limpia los mensajes
    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = "",
                successMessage = ""
            )
        }
    }
}