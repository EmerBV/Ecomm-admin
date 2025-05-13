package com.emerbv.ecommadmin.features.categories.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.categories.domain.AddCategoryUseCase
import com.emerbv.ecommadmin.features.categories.domain.UpdateCategoryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryFormUiState(
    val categoryId: Long? = null,
    val categoryName: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = ""
)

class CategoryFormViewModel(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(CategoryFormUiState())
    val uiState: StateFlow<CategoryFormUiState> = _uiState.asStateFlow()

    /**
     * Inicializa el formulario con los datos de una categoría existente
     */
    fun initWithCategory(category: CategoryDto) {
        _uiState.update {
            it.copy(
                categoryId = category.id,
                categoryName = category.name,
                isSuccess = false,
                errorMessage = ""
            )
        }
    }

    /**
     * Resetea el formulario para añadir una nueva categoría
     */
    fun resetForm() {
        _uiState.update {
            CategoryFormUiState()
        }
    }

    /**
     * Actualiza el nombre de la categoría en el estado
     */
    fun updateCategoryName(name: String) {
        _uiState.update {
            it.copy(categoryName = name, errorMessage = "")
        }
    }

    /**
     * Añade una nueva categoría
     */
    fun addCategory() {
        val categoryName = _uiState.value.categoryName.trim()

        if (categoryName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Category name cannot be empty") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            addCategoryUseCase(categoryName).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = ""
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

    /**
     * Actualiza una categoría existente
     */
    fun updateCategory() {
        val categoryId = _uiState.value.categoryId
        val categoryName = _uiState.value.categoryName.trim()

        if (categoryId == null) {
            _uiState.update { it.copy(errorMessage = "Category ID is missing") }
            return
        }

        if (categoryName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Category name cannot be empty") }
            return
        }

        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }

            updateCategoryUseCase(categoryId, categoryName).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = ""
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
}