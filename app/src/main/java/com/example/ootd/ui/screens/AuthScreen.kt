package com.example.ootd.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ootd.R
import com.example.ootd.data.AppState
import com.example.ootd.data.db.UserDatabaseHelper
import com.example.ootd.ui.theme.*

enum class AuthMode { LOGIN, SIGN_UP }

@Composable
fun AuthScreen(
    initialMode: AuthMode = AuthMode.LOGIN,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { UserDatabaseHelper.getInstance(context) }

    var currentMode by remember { mutableStateOf(initialMode) }

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var emailOrUsername by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(16.dp))

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(OotdWhite)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = OotdTextPrimary)
        }

        Spacer(Modifier.height(24.dp))

        // Header Title
        Text(
            text = "OOTD",
            style = MaterialTheme.typography.headlineLarge,
            color = OotdTextPrimary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (currentMode == AuthMode.LOGIN) "Welcome back. Sign in to your account." else "Create your account to start styling.",
            style = MaterialTheme.typography.bodyLarge,
            color = OotdTextSecondary
        )

        Spacer(Modifier.height(28.dp))

        // Auth Mode Toggle (Sign In / Sign Up)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(OotdCardBg)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (currentMode == AuthMode.LOGIN) OotdBlack else Color.Transparent)
                    .clickable {
                        currentMode = AuthMode.LOGIN
                        errorMessage = null
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    fontWeight = FontWeight.SemiBold,
                    color = if (currentMode == AuthMode.LOGIN) OotdWhite else OotdTextSecondary,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (currentMode == AuthMode.SIGN_UP) OotdBlack else Color.Transparent)
                    .clickable {
                        currentMode = AuthMode.SIGN_UP
                        errorMessage = null
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    fontWeight = FontWeight.SemiBold,
                    color = if (currentMode == AuthMode.SIGN_UP) OotdWhite else OotdTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Error message banner
        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let { errorText ->
                Surface(
                    color = Color(0xFFFDE8E8),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = errorText,
                        color = Color(0xFFC5221F),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }

        if (currentMode == AuthMode.LOGIN) {
            // LOGIN FORM
            Text("Email or Username", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = emailOrUsername,
                onValueChange = {
                    emailOrUsername = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. juandelacruz or juan@example.com", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Password", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = { Text("Enter password", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = OotdTextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

        } else {
            // SIGN UP FORM
            Text("Full Name", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. Juan Delacruz", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("Username", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. juandelacruz", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("Email Address", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                placeholder = { Text("e.g. juan@example.com", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("Password", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                placeholder = { Text("At least 6 characters", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = OotdTextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("Confirm Password", style = MaterialTheme.typography.labelLarge, color = OotdTextPrimary)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                placeholder = { Text("Re-enter password", color = OotdGray) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OotdWhite,
                    unfocusedContainerColor = OotdWhite,
                    focusedBorderColor = OotdBlack,
                    unfocusedBorderColor = OotdLightGray
                ),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = OotdTextSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(28.dp))

        // Submit Button
        Button(
            onClick = {
                errorMessage = null
                if (currentMode == AuthMode.LOGIN) {
                    val result = dbHelper.loginUser(emailOrUsername, password)
                    result.fold(
                        onSuccess = { user ->
                            AppState.login(user)
                            onAuthSuccess()
                        },
                        onFailure = { ex ->
                            errorMessage = ex.message ?: "Authentication failed"
                        }
                    )
                } else {
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    val result = dbHelper.registerUser(
                        fullName = fullName,
                        username = username,
                        email = email,
                        password = password
                    )
                    result.fold(
                        onSuccess = { user ->
                            AppState.login(user)
                            onAuthSuccess()
                        },
                        onFailure = { ex ->
                            errorMessage = ex.message ?: "Registration failed"
                        }
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
            shape = RoundedCornerShape(28.dp),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = if (currentMode == AuthMode.LOGIN) "Sign In" else "Create Account",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}
