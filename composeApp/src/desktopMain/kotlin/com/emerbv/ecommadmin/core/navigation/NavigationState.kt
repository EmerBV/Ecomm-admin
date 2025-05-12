package com.emerbv.ecommadmin.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse

sealed class Screen {
    data object Login : Screen()
    data class Dashboard(val userData: JwtResponse) : Screen()
}

class NavigationState(
    val currentScreen: MutableState<Screen>
) {
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }
}

@Composable
fun rememberNavigationState(
    initialScreen: Screen = Screen.Login
): NavigationState {
    val currentScreen = remember { mutableStateOf<Screen>(initialScreen) }
    return remember { NavigationState(currentScreen) }
}