package com.gelsoncosta.gacademics.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.gelsoncosta.gacademics.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val AccentColor = Color(0xFF6B8AFE)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)
private val ErrorRed = Color(0xFFFF6B6B)
private val SuccessGreen = Color(0xFF4CAF50)

data class ValidationState(
    val isValid: Boolean,
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Validation states
    var nameValidation by remember { mutableStateOf(ValidationState(true)) }
    var emailValidation by remember { mutableStateOf(ValidationState(true)) }
    var passwordValidation by remember { mutableStateOf(ValidationState(true)) }

    // Validation functions
    fun validateName(input: String): ValidationState {
        return when {
            input.isBlank() -> ValidationState(false, "Nome é obrigatório")
            input.length < 3 -> ValidationState(false, "Nome deve ter pelo menos 3 caracteres")
            else -> ValidationState(true)
        }
    }

    fun validateEmail(input: String): ValidationState {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return when {
            input.isBlank() -> ValidationState(false, "Email é obrigatório")
            !input.matches(emailRegex.toRegex()) -> ValidationState(false, "Email inválido")
            else -> ValidationState(true)
        }
    }

    fun validatePassword(input: String): ValidationState {
        return when {
            input.isBlank() -> ValidationState(false, "Senha é obrigatória")
            input.length < 8 -> ValidationState(false, "Senha deve ter pelo menos 8 caracteres")
            !input.any { it.isDigit() } -> ValidationState(false, "Senha deve conter pelo menos um número")
            !input.any { it.isUpperCase() } -> ValidationState(false, "Senha deve conter pelo menos uma letra maiúscula")
            !input.any { it.isLowerCase() } -> ValidationState(false, "Senha deve conter pelo menos uma letra minúscula")
            !input.any { it in "!@#$%^&*()_+-=[]{}|;:,.<>?".toCharArray() } -> 
                ValidationState(false, "Senha deve conter pelo menos um caractere especial")
            else -> ValidationState(true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Criar Conta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Junte-se à nossa comunidade acadêmica.",
                fontSize = 16.sp,
                color = TextGray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameValidation = validateName(it)
                },
                isError = !nameValidation.isValid,
                label = { Text("Nome Completo", color = TextGray) },
                supportingText = {
                    if (!nameValidation.isValid) {
                        Text(nameValidation.errorMessage ?: "", color = ErrorRed)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name",
                        tint = if (!nameValidation.isValid) ErrorRed else AccentColor
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = AccentColor,
                    errorBorderColor = ErrorRed
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailValidation = validateEmail(it)
                },
                isError = !emailValidation.isValid,
                label = { Text("Email", color = TextGray) },
                supportingText = {
                    if (!emailValidation.isValid) {
                        Text(emailValidation.errorMessage ?: "", color = ErrorRed)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = if (!emailValidation.isValid) ErrorRed else AccentColor
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = AccentColor,
                    errorBorderColor = ErrorRed
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordValidation = validatePassword(it)
                },
                isError = !passwordValidation.isValid,
                label = { Text("Password", color = TextGray) },
                supportingText = {
                    if (!passwordValidation.isValid) {
                        Text(passwordValidation.errorMessage ?: "", color = ErrorRed)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = if (!passwordValidation.isValid) ErrorRed else AccentColor
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password",
                            tint = if (!passwordValidation.isValid) ErrorRed else AccentColor
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = AccentColor,
                    errorBorderColor = ErrorRed
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = ErrorRed,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    // Validate all fields before submission
                    nameValidation = validateName(name)
                    emailValidation = validateEmail(email)
                    passwordValidation = validatePassword(password)
                    
                    if (nameValidation.isValid && emailValidation.isValid && passwordValidation.isValid) {
                        isLoading = true
                        errorMessage = null
                        viewModel.register(
                            name = name,
                            email = email,
                            password = password,
                            onSuccess = {
                                isLoading = false
                                showSuccessMessage = true
                                viewModel.viewModelScope.launch {
                                    delay(1500)
                                    onNavigateToHome()
                                }
                            },
                            onError = { error ->
                                isLoading = false
                                errorMessage = error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                enabled = !isLoading && name.isNotBlank() &&
                        email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = TextWhite,
                    disabledContainerColor = AccentColor.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextWhite
                    )
                } else {
                    Text(
                        "Criar Conta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = showSuccessMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Bem vindo à nossa comunidade acadêmica!",
                            color = SuccessGreen
                        )
                    }
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AccentColor
                )
            ) {
                Text("Já tem uma conta? Clique aquí!")
            }
        }
    }
}