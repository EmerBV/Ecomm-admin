package com.emerbv.ecommadmin

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore
import com.emerbv.ecommadmin.core.di.appModule
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.navigation.rememberNavigationState
import com.emerbv.ecommadmin.core.ui.theme.rememberThemeState
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.auth.presentation.LoginScreenWithRememberMe
import com.emerbv.ecommadmin.features.auth.presentation.LoginViewModel
import com.emerbv.ecommadmin.features.dashboard.presentation.DashboardScreen
import com.emerbv.ecommadmin.features.products.di.productModule
import com.emerbv.ecommadmin.features.products.presentation.ProductDetailScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductEditScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductListScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductListViewModel
import com.emerbv.ecommadmin.features.products.presentation.ProductEditViewModel
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import java.util.prefs.Preferences

fun main() = application {
    val state = rememberWindowState(
        width = 1200.dp,
        height = 900.dp
    )

    // Configurar preferencias para almacenamiento
    val preferences = Preferences.userNodeForPackage(EcommAdmin::class.java)
    val settings = PreferencesSettings(preferences)
    val credentialsDataStore = CredentialsDataStore(settings)
    val tokenManager = TokenManager(settings)

    // Módulo de dependencias adicional
    val platformModule = module {
        single { credentialsDataStore }
        single { tokenManager }
    }

    // Inicializar Koin
    startKoin {
        modules(appModule, platformModule, productModule)
    }

    // Obtener ViewModels
    val loginViewModel = get<LoginViewModel>(LoginViewModel::class.java)
    val productListViewModel = get<ProductListViewModel>(ProductListViewModel::class.java)
    val productEditViewModel = get<ProductEditViewModel>(ProductEditViewModel::class.java)
    val tokenManagerInstance = get<TokenManager>(TokenManager::class.java)

    // Verificar si hay una sesión activa
    val token = credentialsDataStore.getToken()
    val userId = credentialsDataStore.getUserId()

    // Estado inicial de la navegación
    val initialScreen = if (token != null && userId != null) {
        Screen.Dashboard(
            com.emerbv.ecommadmin.features.auth.data.model.JwtResponse(
                id = userId,
                token = token
            )
        )
    } else {
        Screen.Login
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "EcommAdmin",
        resizable = false,
        state = state
    ) {
        val navigationState = rememberNavigationState(initialScreen)
        val themeState = rememberThemeState(credentialsDataStore)
        val currentScreen = navigationState.currentScreen.value

        when (currentScreen) {
            is Screen.Login -> {
                val uiState by loginViewModel.uiState.collectAsState()
                val jwtResponse = uiState.jwtResponse

                // Si el usuario está autenticado, navegar al dashboard
                if (uiState.isLoggedIn && jwtResponse != null) {
                    navigationState.navigateTo(Screen.Dashboard(jwtResponse))
                }

                LoginScreenWithRememberMe(
                    viewModel = loginViewModel,
                    credentialsDataStore = credentialsDataStore,
                    themeState = themeState,
                    onLoginSuccess = {
                        val latestJwtResponse = loginViewModel.uiState.value.jwtResponse
                        if (latestJwtResponse != null) {
                            navigationState.navigateTo(Screen.Dashboard(latestJwtResponse))
                        }
                    }
                )
            }
            is Screen.Dashboard -> {
                DashboardScreen(
                    userData = currentScreen.userData,
                    navigationState = navigationState,
                    tokenManager = tokenManagerInstance
                )
            }
            is Screen.ProductList -> {
                ProductListScreen(
                    viewModel = productListViewModel,
                    onProductSelected = { product ->
                        navigationState.navigateTo(
                            Screen.ProductDetail(
                                userData = currentScreen.userData,
                                product = product
                            )
                        )
                    },
                    onBackClick = {
                        navigationState.navigateTo(Screen.Dashboard(currentScreen.userData))
                    }
                )
            }
            is Screen.ProductDetail -> {
                ProductDetailScreen(
                    product = currentScreen.product,
                    onBackClick = {
                        navigationState.navigateTo(Screen.ProductList(currentScreen.userData))
                    },
                    onEditClick = { product ->
                        navigationState.navigateTo(
                            Screen.ProductEdit(
                                userData = currentScreen.userData,
                                product = product
                            )
                        )
                    },
                    onDeleteClick = {}
                )
            }
            is Screen.ProductEdit -> {
                ProductEditScreen(
                    product = currentScreen.product,
                    viewModel = productEditViewModel,
                    onSaveClick = { updatedProduct ->
                        // Aquí se implementaría la lógica para guardar los cambios
                        // Para este ejemplo, simplemente volvemos a la pantalla de detalle
                        navigationState.navigateTo(
                            Screen.ProductDetail(
                                userData = currentScreen.userData,
                                product = updatedProduct
                            )
                        )
                    },
                    onCancelClick = {
                        // Volver a la pantalla anterior (detalle o lista)
                        navigationState.navigateTo(
                            Screen.ProductDetail(
                                userData = currentScreen.userData,
                                product = currentScreen.product
                            )
                        )
                    }
                )
            }
        }
    }
}

class EcommAdmin