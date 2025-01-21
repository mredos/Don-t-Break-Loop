import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.acmarge.viewmodel.UserProfileViewModel
import com.example.acmarge.ui.components.CustomBottomBar


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MainHome(viewModel: UserProfileViewModel) {
    val navController = rememberNavController()

    // Dialog görünürlüğü için state
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                onHomeClick = { navController.navigate("home") },
                onCalendarClick = { navController.navigate("calendar") },
                onAddTaskClick = { showDialog = true }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Ana Sayfa
            composable("home") {
                HomeScreen(
                    onProfileClick = { navController.navigate("profile") },
                    onNotificationClick = { navController.navigate("notifications") }
                )
            }

            // Bildirimler
            composable("notifications") {
                NotificationScreen(
                    notifications = listOf(),
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Profil
            composable("profile") {
                UserProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEdit = { navController.navigate("editProfile") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Profil Düzenleme
            composable("editProfile") {
                val userState by viewModel.user.observeAsState()
                userState?.let { user ->
                    UserProfileEditScreen(
                        userName = user.name ?: "",
                        userProfession = user.profession ?: "",
                        userEmail = user.email ?: "",
                        currentPhoto = user.profilePhoto,
                        onSave = { newName, newProfession, newEmail, newPhotoUri ->
                            viewModel.updateUserProfile(
                                name = newName,
                                profession = newProfession,
                                email = newEmail,
                                profilePhoto = newPhotoUri
                            )
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    // Eğer `showDialog` true ise dialog göster
    if (showDialog) {
        AddTaskTakePhotoDialog(
            onDismiss = { showDialog = false },
            onAddTaskClick = {
                showDialog = false
                // "Add Task" ekranına yönlendirme
                navController.navigate("addTaskScreen")
            },
            onTakePhotoClick = {
                showDialog = false
                // Kamera ekranına yönlendirme
                navController.navigate("cameraScreen")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BreakLoop") },
                actions = {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.Gray
                        )
                    }
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Text("Welcome to Home Screen")
            }
        }
    )
}