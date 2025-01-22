import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.acmarge.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onNavigateToEdit: () -> Unit,
    onNavigateBack: () -> Unit  // Geri tuşuna basınca çalışacak callback
) {
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.loading.observeAsState(false)

    // Çıkış (logout) için açılacak dialog kontrolü
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Scaffold, üstte TopAppBar gibi sabit bileşenleri yönetmek için kullanılır
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                )
            )
        }
    ) { innerPadding ->
        // Scaffold içindeki içerik alanı (padding ile birlikte)
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

            // Eğer veriler yükleniyorsa progress indicator göster
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                user?.let { userData ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        // Header bölümü (gradient arka plan)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF4285F4),
                                            Color(0xFF42A5F5)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                AsyncImage(
                                    model = userData.profilePhoto,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop, // Görselin alanı tamamen doldurmasını sağlar
                                    modifier = Modifier
                                        .size(100.dp) // Daire boyutu
                                        .clip(CircleShape) // Daire şekli
                                        .background(Color.White, CircleShape) // Beyaz arka plan
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = userData.name ?: "No Name",
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )

                            }
                        }

                        // Haftalık başarı oranı bölümü
                        Column(
                            modifier = Modifier
                                .fillMaxWidth() // Column'un genişliğini ekranın tamamına yay
                                .padding(10.dp), // Kenar boşlukları
                            horizontalAlignment = Alignment.CenterHorizontally // İçeriği yatayda ortala
                        ) {
                            Text(
                                text = "Haftalık başarı oranım",
                                fontSize = 18.sp,
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(100.dp) // Box boyutu
                            ) {
                                CircularProgressIndicator(
                                    progress = 0.77f,
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFF4285F4),
                                    strokeWidth = 8.dp
                                )
                                Text(
                                    text = "77%",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4285F4)
                                )
                            }
                        }

                        // Menü öğeleri
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            MenuItem(
                                title = "My Profile",
                                onClick = onNavigateToEdit
                            )
                            MenuItem(
                                title = "Statistic",
                                onClick = { /* İstatistik ekranına geçiş */ }
                            )
                            MenuItem(
                                title = "Logout",
                                onClick = { showLogoutDialog = true }
                            )
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("User data not available")
                    }
                }
            }
        }
    }

    // Logout onayı için AlertDialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Log Out",
                    color = Color.Black,  // Başlık rengi
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "Are you sure you want to log out?",
                    color = Color.Gray  // Metin rengi
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Burada gerçek logout işlemini yapabilirsiniz.
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, // Logout buton arka plan
                        contentColor = Color.White   // Logout buton metin rengi
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray, // Cancel buton arka plan
                        contentColor = Color.Black         // Cancel buton metin rengi
                    )
                ) {
                    Text("Cancel")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}