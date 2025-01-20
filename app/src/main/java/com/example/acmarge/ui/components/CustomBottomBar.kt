package com.example.acmarge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acmarge.R

@Composable
fun CustomBottomBar(
    onHomeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAddTaskClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp) // Alt barın yüksekliğini artırdık
    ) {
        // Ortadaki yuvarlak buton
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(y = (-6).dp) // Daha belirgin olması için yukarı taşıdık
                .align(Alignment.TopCenter)
                .background(Color(0xFF006EE9), CircleShape)
                .clickable { onAddTaskClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Alt barın sol ve sağ ikonları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp)
                .height(64.dp), // Alt bar için yüksekliği sabit tuttuk
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol İkon (Ev)
            IconButton(onClick = onHomeClick) {
                Icon(
                    painter = painterResource(id = R.drawable.houseicon),
                    contentDescription = "Home Icon",
                    tint = Color(0xFF006EE9), // Ev simgesini mavi yaptık
                    modifier = Modifier.size(32.dp)
                )
            }

            // Sağ İkon (Takvim)
            IconButton(onClick = onCalendarClick) {
                Icon(
                    painter = painterResource(id = R.drawable.calendaricon),
                    contentDescription = "Calendar Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
