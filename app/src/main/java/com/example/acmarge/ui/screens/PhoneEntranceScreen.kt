package com.example.acmarge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.acmarge.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun PhoneEntranceScreen(
    userData: UserRegistrationData, // Kullanıcıdan alınan bilgileri içeren nesne
    onNavigateToVerify: (UserRegistrationData, phoneNumber: String, verificationId: String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Firebase callbacks
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Otomatik doğrulama (Play Services aracılığıyla)
            // Gerekirse doğrudan kayıt işlemi burada yapılabilir.
        }

        override fun onVerificationFailed(e: FirebaseException) {
            isLoading = false
            statusText = "Verification failed: ${e.message}"
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            isLoading = false
            statusText = "SMS code sent to $phoneNumber"
            // Kullanıcıyı VerifyScreen'e yönlendirirken verificationId'yi ekliyoruz
            onNavigateToVerify(userData, phoneNumber, verificationId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Beyaz arka plan
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Başlık
            Text(text = "Enter your phone number", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Telefon numarası alanı
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number (e.g., +905xxxxxxxxx)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Verify butonu
            Button(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        isLoading = true
                        statusText = "Sending SMS..."
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as MainActivity) // Firebase doğrulama için Activity gerekiyor
                            .setCallbacks(callbacks)
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    } else {
                        statusText = "Please enter a valid phone number."
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006EE9),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(text = "Verify")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Durum metni
            Text(text = statusText, color = Color.Gray)
        }
    }
}
