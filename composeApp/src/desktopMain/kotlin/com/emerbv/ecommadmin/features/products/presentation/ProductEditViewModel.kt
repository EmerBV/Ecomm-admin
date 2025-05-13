package com.emerbv.ecommadmin.features.products.presentation

import com.emerbv.ecommadmin.features.products.data.model.CategoryDto
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
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // Estado para las categorías
    private val _categoryState = MutableStateFlow(CategoryUiState(isLoading = true))
    val categoryState: StateFlow<CategoryUiState> = _categoryState.asStateFlow()

    /**
     * Carga las categorías desde el backend
     */
    fun loadCategories() {
        scope.launch {
            try {
                _categoryState.update { it.copy(isLoading = true, errorMessage = null) }

                // Simulamos una llamada al API para cargar las categorías
                // En una implementación real, aquí llamaríamos al repositorio
                val categories = fetchCategories()

                _categoryState.update {
                    it.copy(
                        categories = categories,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _categoryState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading categories: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Método que simula obtener las categorías desde el backend
     * En una implementación real, esto estaría en el repositorio y usaría Retrofit/Ktor
     */
    private suspend fun fetchCategories(): List<CategoryDto> {
        // Simulamos un delay de red
        kotlinx.coroutines.delay(500)

        // Retornamos una lista de categorías de ejemplo
        return listOf(
            CategoryDto(id = 1, name = "Dragon Ball"),
            CategoryDto(id = 2, name = "One Piece"),
            CategoryDto(id = 3, name = "Naruto"),
            CategoryDto(id = 4, name = "Bleach"),
            CategoryDto(id = 5, name = "Demon Slayer"),
            CategoryDto(id = 6, name = "My Hero Academia"),
            CategoryDto(id = 7, name = "Attack on Titan"),
            CategoryDto(id = 8, name = "Jujutsu Kaisen")
        )
    }
}