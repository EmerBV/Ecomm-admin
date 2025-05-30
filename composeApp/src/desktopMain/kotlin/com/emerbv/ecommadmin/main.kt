package com.emerbv.ecommadmin

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore
import com.emerbv.ecommadmin.core.di.appModule
import com.emerbv.ecommadmin.core.navigation.Screen
import com.emerbv.ecommadmin.core.navigation.rememberNavigationState
import com.emerbv.ecommadmin.core.session.SessionManagerProvider
import com.emerbv.ecommadmin.core.session.SessionScreen
import com.emerbv.ecommadmin.core.ui.theme.rememberThemeState
import com.emerbv.ecommadmin.core.utils.TokenManager
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.auth.presentation.LoginScreenWithRememberMe
import com.emerbv.ecommadmin.features.auth.presentation.LoginViewModel
import com.emerbv.ecommadmin.features.categories.di.categoryModule
import com.emerbv.ecommadmin.features.categories.presentation.CategoryAddScreen
import com.emerbv.ecommadmin.features.categories.presentation.CategoryEditScreen
import com.emerbv.ecommadmin.features.categories.presentation.CategoryFormViewModel
import com.emerbv.ecommadmin.features.categories.presentation.CategoryListScreen
import com.emerbv.ecommadmin.features.categories.presentation.CategoryListViewModel
import com.emerbv.ecommadmin.features.dashboard.di.dashboardModule
import com.emerbv.ecommadmin.features.dashboard.presentation.DashboardScreen
import com.emerbv.ecommadmin.features.dashboard.presentation.DashboardViewModel
import com.emerbv.ecommadmin.features.products.di.productModule
import com.emerbv.ecommadmin.features.products.presentation.ProductDetailScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductFormScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductListScreen
import com.emerbv.ecommadmin.features.products.presentation.ProductListViewModel
import com.emerbv.ecommadmin.features.products.presentation.ProductFormViewModel
import com.emerbv.ecommadmin.features.products.presentation.ProductVariantsViewModel
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import java.util.concurrent.TimeUnit
import java.util.prefs.Preferences

fun main() = application {
    val state = rememberWindowState(
        width = 1400.dp,
        height = 1000.dp
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

    // Inicializar Koin con todos los módulos
    startKoin {
        modules(appModule, platformModule, productModule, categoryModule, dashboardModule)
    }

    // Obtener ViewModels
    val loginViewModel = get<LoginViewModel>(LoginViewModel::class.java)
    val productListViewModel = get<ProductListViewModel>(ProductListViewModel::class.java)
    val productFormViewModel = get<ProductFormViewModel>(ProductFormViewModel::class.java)
    val productVariantsViewModel = get<ProductVariantsViewModel>(ProductVariantsViewModel::class.java)
    val categoryListViewModel = get<CategoryListViewModel>(CategoryListViewModel::class.java)
    val categoryFormViewModel = get<CategoryFormViewModel>(CategoryFormViewModel::class.java)
    val dashboardViewModel = get<DashboardViewModel>(DashboardViewModel::class.java)

    // Verificar si hay una sesión activa usando TokenManager
    val token = tokenManager.getToken(forceReload = true)
    val userId = tokenManager.getUserId(forceReload = true)

    // Verificar timeout al inicio y limpiar sesión si es necesario
    if (token != null && userId != null) {
        if (tokenManager.hasSessionTimedOut(TimeUnit.MINUTES.toMillis(30))) {
            println("Sesión caducada por inactividad al iniciar la aplicación")
            tokenManager.clearSession()
            credentialsDataStore.clearCredentials()
        } else {
            println("Sesión activa válida, actualizando timestamp de actividad")
            tokenManager.updateLastActivityTimestamp()
        }
    }

// Determinar pantalla inicial después de verificar timeout
    val initialScreen = if (tokenManager.isLoggedIn()) {
        val currentUserId = tokenManager.getUserId() ?: throw IllegalStateException("User ID missing after isLoggedIn check")
        val currentToken = tokenManager.getToken() ?: throw IllegalStateException("Token missing after isLoggedIn check")

        Screen.Dashboard(
            JwtResponse(
                id = currentUserId,
                token = currentToken
            )
        )
    } else {
        Screen.Login
    }

    val navigationState = rememberNavigationState(initialScreen)
    val themeState = rememberThemeState(credentialsDataStore)

    Window(
        onCloseRequest = ::exitApplication,
        title = "EcommAdmin",
        resizable = false,
        state = state
    ) {
        // Verificar consistencia de sesión al iniciar
        val token = tokenManager.getToken()
        val userId = tokenManager.getUserId()

        println("Estado de sesión al iniciar:")
        println("- Token: ${token?.take(10) ?: "null"}")
        println("- User ID: $userId")

        // Verificar timeout al inicio de manera explícita
        if (token != null && userId != null) {
            if (tokenManager.hasSessionTimedOut(TimeUnit.MINUTES.toMillis(30))) {
                println("Sesión caducada por inactividad al iniciar")
                tokenManager.clearSession()
                // Forzar estado a Login explícitamente
                navigationState.currentScreen.value = Screen.Login
            } else {
                println("Sesión activa válida")
                // Actualizar actividad
                tokenManager.updateLastActivityTimestamp()
            }
        }

        val currentScreen = navigationState.currentScreen.value

        SessionManagerProvider(
            tokenManager = tokenManager,
            navigationState = navigationState,
            credentialsDataStore = credentialsDataStore
        ) {
            when (currentScreen) {
                is Screen.Login -> {
                    // Don't automatically log in when coming from logout
                    val isFromLogout = remember { mutableStateOf(false) }

                    // Check if we're coming from logout
                    LaunchedEffect(Unit) {
                        isFromLogout.value = navigationState.previousScreen is Screen.Dashboard
                    }

                    LoginScreenWithRememberMe(
                        viewModel = loginViewModel,
                        credentialsDataStore = credentialsDataStore,
                        themeState = themeState,
                        tokenManager = tokenManager,
                        preventAutoLogin = isFromLogout.value,  // Pass this to login screen
                        onLoginSuccess = {
                            val latestJwtResponse = loginViewModel.uiState.value.jwtResponse
                            if (latestJwtResponse != null) {
                                tokenManager.updateLastActivityTimestamp()
                                navigationState.navigateTo(Screen.Dashboard(latestJwtResponse))
                            }
                        }
                    )
                }

                // Pantallas protegidas que requieren autenticación
                // Todas envueltas en SessionScreen para monitoreo de actividad automático
                is Screen.Dashboard -> {
                    SessionScreen {
                        DashboardScreen(
                            userData = currentScreen.userData,
                            navigationState = navigationState,
                            tokenManager = tokenManager,
                            viewModel = dashboardViewModel
                        )
                    }
                }
                is Screen.ProductList -> {
                    SessionScreen {
                        ProductListScreen(
                            viewModel = productListViewModel,
                            userData = currentScreen.userData,
                            navigationState = navigationState,
                            tokenManager = tokenManager
                        )
                    }
                }
                is Screen.ProductAdd -> {
                    SessionScreen {
                        ProductFormScreen(
                            isNewProduct = true,
                            initialProduct = null,
                            viewModel = productFormViewModel,
                            onSaveClick = { newProduct ->
                                navigationState.navigateTo(Screen.ProductList(currentScreen.userData))
                            },
                            onCancelClick = {
                                navigationState.navigateTo(Screen.ProductList(currentScreen.userData))
                            }
                        )
                    }
                }
                is Screen.ProductEdit -> {
                    SessionScreen {
                        ProductFormScreen(
                            isNewProduct = false,
                            initialProduct = currentScreen.product,
                            viewModel = productFormViewModel,
                            onSaveClick = { updatedProduct ->
                                navigationState.navigateTo(
                                    Screen.ProductDetail(
                                        userData = currentScreen.userData,
                                        product = updatedProduct
                                    )
                                )
                            },
                            onCancelClick = {
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
                is Screen.ProductDetail -> {
                    SessionScreen {
                        productVariantsViewModel.initWithProduct(currentScreen.product)

                        ProductDetailScreen(
                            product = currentScreen.product,
                            variantsViewModel = productVariantsViewModel,
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
                            onDeleteClick = { /* Implementar eliminación */ }
                        )
                    }
                }
                is Screen.CategoryList -> {
                    SessionScreen {
                        CategoryListScreen(
                            viewModel = categoryListViewModel,
                            userData = currentScreen.userData,
                            navigationState = navigationState,
                            onBackClick = {
                                navigationState.navigateTo(Screen.Dashboard(currentScreen.userData))
                            }
                        )
                    }
                }
                is Screen.CategoryAdd -> {
                    SessionScreen {
                        CategoryAddScreen(
                            viewModel = categoryFormViewModel,
                            onSaveComplete = {
                                navigationState.navigateTo(Screen.CategoryList(currentScreen.userData))
                            },
                            onCancel = {
                                navigationState.navigateTo(Screen.CategoryList(currentScreen.userData))
                            }
                        )
                    }
                }
                is Screen.CategoryEdit -> {
                    SessionScreen {
                        CategoryEditScreen(
                            category = currentScreen.category,
                            viewModel = categoryFormViewModel,
                            onSaveComplete = {
                                navigationState.navigateTo(Screen.CategoryList(currentScreen.userData))
                            },
                            onCancel = {
                                navigationState.navigateTo(Screen.CategoryList(currentScreen.userData))
                            }
                        )
                    }
                }
            }
        }
    }
}

class EcommAdmin