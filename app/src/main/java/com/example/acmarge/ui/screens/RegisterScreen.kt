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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var auth: FirebaseAuth


data class UserRegistrationData(
    val fullName: String,
    val email: String,
    val password: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPhoneEntrance: (UserRegistrationData) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .wrapContentSize(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Başlık
                Text(
                    text = "Create your\nAccount",
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                    color = Color(0xFF323142),
                    lineHeight = 44.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                )

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.usericon),
                            contentDescription = "User Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter Your Email") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.emailicon),
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.lockicon),
                            contentDescription = "Lock Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    trailingIcon = {
                        val iconRes = if (isPasswordVisible) R.drawable.showpassword else R.drawable.eyeofficon
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Toggle Password Visibility",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    isPasswordVisible = !isPasswordVisible
                                }
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            registerUser(email, password, onSuccess = {
                                // Kayıt başarılı, bir sonraki ekrana yönlendirme
                                onNavigateToPhoneEntrance(
                                    UserRegistrationData(fullName = "", email = email, password = password)
                                )
                            }, onFailure = { errorMessage ->
                                // Hata durumunda kullanıcıya mesaj göster
                                println("Hata: $errorMessage")
                            })
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006EE9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Register",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Alt kısım: "Already have an account? Sign In"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account?",
                        color = Color(0xFFCBCBCB),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign In",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onNavigateToLogin()
                        }
                    )
                }
            }
        }
    }
}

fun registerUser(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Kullanıcı Firestore'a ekleniyor
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val user = mapOf(
                        "email" to email,
                        "name" to "", // İlk kayıt sırasında boş bırak
                        "profession" to "" // İlk kayıt sırasında boş bırak
                    )
                    firestore.collection("Users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Firestore hatası: ${e.message}")
                        }
                } else {
                    onFailure("Kullanıcı ID'si alınamadı.")
                }
            } else {
                onFailure("Kayıt başarısız: ${task.exception?.message}")
            }
        }
}

