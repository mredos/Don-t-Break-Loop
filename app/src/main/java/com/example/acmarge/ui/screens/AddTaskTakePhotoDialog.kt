import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Task
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

@Composable
fun AddTaskTakePhotoDialog(
    onDismiss: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTakePhotoClick: () -> Unit
) {
    // Dialog kullanarak, dışa tıklanınca kapanacak şekilde ayarladık
    Dialog(onDismissRequest = onDismiss) {
        // Surface ile arka plan şeklimizi ayarlayabiliriz
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .wrapContentSize()
        ) {
            // Animasyonlu içerik
            AnimatedVisibility(
                visible = true, // Görünürlük her zaman true, ancak animasyonlu
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(300)), // Yukarıdan aşağı
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(300)) // Aşağıdan yukarı
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Üst bölüme ince çizgi (isteğe bağlı)
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // İki “kart”ı yan yana koymak için Row kullanıyoruz
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Kart (Add Task)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable { onAddTaskClick() }, // Tıklanabilirlik
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4285F4))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Task, // Görev ikonu
                                        contentDescription = "Task Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp) // İkon boyutu
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Add Task",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 2. Kart (Take the Photo)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable { onTakePhotoClick() }, // Tıklanabilirlik
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3C1A73))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt, // Kamera ikonu
                                        contentDescription = "Camera Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp) // İkon boyutu
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Take Photo",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}