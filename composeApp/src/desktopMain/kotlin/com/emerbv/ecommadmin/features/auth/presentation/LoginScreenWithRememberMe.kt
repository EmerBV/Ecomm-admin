package com.emerbv.ecommadmin.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.datastore.CredentialsDataStore
import com.emerbv.ecommadmin.core.ui.components.EmailTextField
import com.emerbv.ecommadmin.core.ui.components.PasswordTextField
import com.emerbv.ecommadmin.core.ui.components.PrimaryButton
import com.emerbv.ecommadmin.core.ui.components.ThemeToggleButton
import com.emerbv.ecommadmin.core.ui.theme.AppTheme
import com.emerbv.ecommadmin.core.ui.theme.ThemeState

/**
 * Pantalla de login mejorada con funcionalidad "Recordarme" y selector de tema.
 */
@Composable
fun LoginScreenWithRememberMe(
    viewModel: LoginViewModel,
    credentialsDataStore: CredentialsDataStore,
    themeState: ThemeState,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var rememberMe by remember { mutableStateOf(credentialsDataStore.getRememberMe()) }

    // Si hay credenciales guardadas, rellenar los campos
    LaunchedEffect(Unit) {
        if (credentialsDataStore.hasCredentials()) {
            credentialsDataStore.getEmail()?.let { viewModel.validateEmail(it) }
            credentialsDataStore.getPassword()?.let { viewModel.validatePassword(it) }
        }
    }

    // Si el login es exitoso, almacenar credenciales y navegar
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && uiState.jwtResponse != null) {
            // Guardar credenciales si "recordarme" está activado
            credentialsDataStore.saveCredentials(
                uiState.email,
                uiState.password,
                rememberMe
            )

            // Guardar información de sesión
            uiState.jwtResponse?.let {
                credentialsDataStore.saveSession(it.token, it.id)
            }

            onLoginSuccess()
        }
    }

    AppTheme(darkTheme = themeState.isDarkMode.value) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Selector de tema en la esquina superior derecha
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeToggleButton(
                        isDarkMode = themeState.isDarkMode.value,
                        onToggle = { themeState.toggleDarkMode() }
                    )
                }

                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        elevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Logo - Usando un Box en lugar de una imagen ya que no tenemos recursos
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 24.dp)
                                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LOGO",
                                    style = MaterialTheme.typography.h6,
                                    color = MaterialTheme.colors.primary
                                )
                            }

                            // Título
                            Text(
                                text = "Ecomm Admin",
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // Formulario
                            EmailTextField(
                                value = uiState.email,
                                onValueChange = { viewModel.validateEmail(it) },
                                errorMessage = uiState.emailError,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            PasswordTextField(
                                value = uiState.password,
                                onValueChange = { viewModel.validatePassword(it) },
                                errorMessage = uiState.passwordError,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Checkbox "Recordarme"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colors.primary
                                    )
                                )

                                Text(
                                    text = "Recordar mis credenciales",
                                    style = MaterialTheme.typography.body2,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            // Mensaje de error general
                            if (uiState.errorMessage != null) {
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.body2,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }

                            // Botón de login
                            PrimaryButton(
                                text = if (uiState.isLoading) "Iniciando sesión..." else "Iniciar sesión",
                                onClick = {
                                    viewModel.login()
                                },
                                enabled = !uiState.isLoading &&
                                        uiState.email.isNotEmpty() &&
                                        uiState.password.isNotEmpty() &&
                                        uiState.emailError == null &&
                                        uiState.passwordError == null
                            )
                        }
                    }
                }

                // Indicador de carga
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
