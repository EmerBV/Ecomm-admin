package com.emerbv.ecommadmin.features.auth.presentation

import com.emerbv.ecommadmin.core.network.ApiResult
import com.emerbv.ecommadmin.core.utils.Validator
import com.emerbv.ecommadmin.features.auth.data.model.JwtResponse
import com.emerbv.ecommadmin.features.auth.domain.LoginUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // Estado para la interfaz de usuario
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Validación de campos
    fun validateEmail(email: String) {
        val isValid = Validator.isValidEmail(email)
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (!isValid && email.isNotEmpty()) "Email no válido" else null
            )
        }
    }

    fun validatePassword(password: String) {
        val isValid = Validator.isValidPassword(password)
        _uiState.update {
            it.copy(
                password = password,
                passwordError = if (!isValid && password.isNotEmpty()) "La contraseña debe tener al menos 6 caracteres" else null
            )
        }
    }

    // Proceso de login
    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        // Validar campos antes de intentar login
        if (!Validator.isValidEmail(email)) {
            _uiState.update { it.copy(emailError = "Email no válido") }
            return
        }

        if (!Validator.isValidPassword(password)) {
            _uiState.update { it.copy(passwordError = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        // Iniciar proceso de login
        scope.launch {
            loginUseCase(email, password).collect { result ->
                _uiState.update {
                    when (result) {
                        is ApiResult.Loading -> it.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                        is ApiResult.Success -> it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            jwtResponse = result.data,
                            errorMessage = null
                        )
                        is ApiResult.Error -> it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    // Reset de errores
    fun resetErrors() {
        _uiState.update {
            it.copy(emailError = null, passwordError = null, errorMessage = null)
        }
    }
}