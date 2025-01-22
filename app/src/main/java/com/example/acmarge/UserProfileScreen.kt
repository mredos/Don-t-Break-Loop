import android.view.MenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    userId: String, // Kullanıcı ID'si
    onNavigateToEdit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Kullanıcı verilerini ve yüklenme durumunu gözlemle
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.loading.observeAsState(false)

    // Girişte kullanıcı verilerini yükle
    LaunchedEffect(userId) {
        viewModel.fetchUserProfile(userId)
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontSize = 18.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4285F4))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AsyncImage(
                                    model = userData.profilePhoto,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.White, CircleShape)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = userData.name,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
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
                                modifier = Modifier.size(100.dp)
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

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            MenuItem(title = "My Profile", onClick = onNavigateToEdit)
                            MenuItem(title = "Statistic", onClick = { /* İstatistik ekranına geçiş */ })
                            MenuItem(title = "Logout", onClick = { showLogoutDialog = true })
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

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", color = Color.Black, fontSize = 18.sp) },
            text = { Text("Are you sure you want to log out?", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Logout işlemi burada yapılabilir
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)
                ) {
                    Text("Cancel")
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}


@Composable
fun MenuItem(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}