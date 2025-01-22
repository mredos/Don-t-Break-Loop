package com.example.acmarge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acmarge.R
import com.example.acmarge.ui.components.CustomBottomBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    onResetConfirmed: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Reset Your\nPassword",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF323142)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Yeni Şifre
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.lockicon),
                            contentDescription = "Lock Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        val iconRes = if (isPasswordVisible) R.drawable.eyeofficon else R.drawable.showpassword
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Toggle Password Visibility",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Şifreyi Tekrar Gir
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.lockicon),
                            contentDescription = "Lock Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        val iconRes = if (isConfirmPasswordVisible) R.drawable.eyeofficon else R.drawable.showpassword
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Toggle Password Visibility",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Hata Mesajı
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset Butonu
                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            if (password.length >= 6) {
                                isLoading = true
                                auth.currentUser?.updatePassword(password)
                                    ?.addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onResetConfirmed()
                                        } else {
                                            errorMessage = task.exception?.message ?: "Password reset failed."
                                        }
                                    }
                            } else {
                                errorMessage = "Password must be at least 6 characters long."
                            }
                        } else {
                            errorMessage = "Passwords do not match."
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006EE9)),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Reset",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
