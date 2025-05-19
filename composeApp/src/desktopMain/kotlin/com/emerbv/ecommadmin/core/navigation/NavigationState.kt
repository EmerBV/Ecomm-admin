package com.emerbv.ecommadmin.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.categories.data.model.CategoryDto
import com.emerbv.ecommadmin.features.products.data.model.ProductDto

sealed class Screen {
    data object Login : Screen()
    data class Dashboard(val userData: JwtResponse) : Screen()

    // Product screens
    data class ProductList(val userData: JwtResponse) : Screen()
    data class ProductDetail(val userData: JwtResponse, val product: ProductDto) : Screen()
    data class ProductEdit(val userData: JwtResponse, val product: ProductDto) : Screen()
    data class ProductAdd(val userData: JwtResponse) : Screen()

    // Category screens
    data class CategoryList(val userData: JwtResponse) : Screen()
    data class CategoryAdd(val userData: JwtResponse) : Screen()
    data class CategoryEdit(val userData: JwtResponse, val category: CategoryDto) : Screen()
}

class NavigationState(
    val currentScreen: MutableState<Screen>
) {
    var previousScreen: Screen? = null
        private set

    fun navigateTo(screen: Screen) {
        println("Navigating to: ${screen::class.simpleName}")

        // Store current screen as previous before changing
        previousScreen = currentScreen.value

        // Update current screen
        currentScreen.value = screen

        // Verification
        if (currentScreen.value != screen) {
            println("ERROR: Navigation did not update state correctly")
        }
    }

    /**
     * Reinicia completamente el estado de navegación a la pantalla de Login
     */
    fun resetToLogin() {
        println("Resetting navigation stack to Login")
        previousScreen = null
        currentScreen.value = Screen.Login
    }
}

@Composable
fun rememberNavigationState(
    initialScreen: Screen = Screen.Login
): NavigationState {
    val currentScreen = remember { mutableStateOf<Screen>(initialScreen) }
    return remember { NavigationState(currentScreen) }
}