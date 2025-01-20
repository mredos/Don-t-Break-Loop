import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomBottomBar(
    onHomeClick: () -> Unit,
    onFabClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp) // Dış kapsayıcı yüksekliği
    ) {
        // Arka plan (Surface), üst köşeleri oval olacak şekilde
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Biraz daha dar, böylece FAB yukarı doğru taşabilir
                .align(Alignment.BottomCenter),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            // İkonları yerleştirmek için Row kullanıyoruz
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol İkon (Home)
                IconButton(onClick = onHomeClick) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color(0xFF4285F4) // Örnek bir renk
                    )
                }
                // Sağ İkon (Calendar)
                IconButton(onClick = onCalendarClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = Color.Gray
                    )
                }
            }
        }

        // Ortadaki büyük "+" (FloatingActionButton)
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = Color(0xFF4285F4),
            modifier = Modifier
                .size(60.dp)             // Büyük buton boyutu
                .align(Alignment.TopCenter) // Box içinde yatay ortala, dikey olarak en üstten başla
                .offset(y = (-20).dp)   // Bir miktar yukarı kaydırıyoruz
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}