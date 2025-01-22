package com.example.acmarge.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalFocusManager
import com.example.acmarge.R
import com.example.acmarge.models.VerifyMode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    userData: UserRegistrationData?,
    phoneNumber: String,
    verificationId: String,
    mode: VerifyMode,
    onNavigateBack: () -> Unit,
    onVerificationSuccess: () -> Unit,
    onSendAgain: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    val codeDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Focus kontrolü için FocusRequester ve FocusManager
    val focusRequesters = List(6) { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Verify Phone", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back Arrow",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Verification Code",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF323142),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We've sent a 6-digit code to your phone number",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = phoneNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF323142),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Altı adet TextField (kutu) otomatik geçişli
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val boxSize =60.dp // Kutuların boyutu artırıldı
                val boxSpacing = 10.dp

                repeat(6) { index ->
                    OutlinedTextField(
                        value = codeDigits[index],
                        onValueChange = { newChar ->
                            if (newChar.length <= 1 && newChar.all { it.isDigit() }) {
                                codeDigits[index] = newChar
                                if (newChar.isNotEmpty() && index < 5) {
                                    // Eğer kutu dolduysa bir sonrakine geç
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 28.sp, // Daha büyük yazı boyutu
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(boxSize)
                            .height(boxSize)
                            .padding(horizontal = boxSpacing / 2)
                            .focusRequester(focusRequesters[index]), // FocusRequester bağlandı

                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val enteredCode = codeDigits.joinToString("")
                    if (enteredCode.length == 6) {
                        isLoading = true
                        val credential: PhoneAuthCredential =
                            PhoneAuthProvider.getCredential(verificationId, enteredCode)

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    when (mode) {
                                        VerifyMode.Register -> {
                                            val uid = auth.currentUser?.uid // Kullanıcı ID'si alınıyor
                                            val email = auth.currentUser?.email // Kullanıcı e-posta bilgisi alınıyor
                                            if (uid != null && email != null) {
                                                // Firestore'a kullanıcı bilgisi ekle
                                                registerUser(
                                                    userId = uid,
                                                    email = email,
                                                    name = "No Name", // Varsayılan değer
                                                    job = "No Job", // Varsayılan değer
                                                    profilePhotoUrl = "" // Varsayılan değer
                                                ) { success ->
                                                    isLoading = false
                                                    if (success) {
                                                        onVerificationSuccess()
                                                    } else {
                                                        errorMessage = "Failed to save user data to Firestore."
                                                    }
                                                }
                                            } else {
                                                isLoading = false
                                                errorMessage = "Failed to retrieve user ID or email."
                                            }
                                        }
                                        VerifyMode.ResetPassword -> {
                                            isLoading = false
                                            onVerificationSuccess()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = task.exception?.message ?: "Invalid code."
                                }
                            }
                    } else {
                        errorMessage = "Please enter the complete code."
                    }
                },
                enabled = !isLoading && codeDigits.all { it.isNotEmpty() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006EE9)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Verify",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (!isLoading) onSendAgain() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3E3E3)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Send Again",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

fun registerUser(
    userId: String,
    email: String,
    name: String = "No Name",
    job: String = "No Job",
    profilePhotoUrl: String = "",
    onResult: (Boolean) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val userProfile = mapOf(
        "name" to name,
        "email" to email,
        "job" to job,
        "profilePhoto" to profilePhotoUrl
    )

    firestore.collection("Profiles").document(userId)
        .set(userProfile)
        .addOnSuccessListener {
            onResult(true) // Firestore'a kaydedildi
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error saving user profile", e)
            onResult(false) // Firestore kaydedilemedi
        }
}
