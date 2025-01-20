package com.example.acmarge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acmarge.R

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Üstteki resim
            Image(
                painter = painterResource(id = R.drawable.brainbox),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp) // 200x200
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Metin: "Welcome to BrainBox" (iki satır halinde)
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.displayMedium.copy( // Daha büyük boyut
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "BrainBox",
                style = MaterialTheme.typography.displayMedium.copy( // Daha büyük boyut
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp)) // Yazı ile butonlar arasındaki boşluğu artırdık

            // Log in Butonu
            Button(
                onClick = { onNavigateToLogin() },
                shape = RoundedCornerShape(50.dp), // Kenarları tam yuvarlat
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006EE9) // Mavi
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = "Log in",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign up Butonu
            Button(
                onClick = { onNavigateToRegister() },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE3E3E3) // Açık Gri
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = "Sign up",
                    color = Color(0xFF8E8E8E), // Buton yazı rengi gri
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
