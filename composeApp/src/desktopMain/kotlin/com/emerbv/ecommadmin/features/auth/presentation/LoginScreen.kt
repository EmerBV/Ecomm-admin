package com.emerbv.ecommadmin.features.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.emerbv.ecommadmin.core.ui.components.EmailTextField
import com.emerbv.ecommadmin.core.ui.components.PasswordTextField
import com.emerbv.ecommadmin.core.ui.components.PrimaryButton
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    EcommAdminTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                                    .clip(RoundedCornerShape(8.dp))
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
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

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
                                onClick = { viewModel.login() },
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