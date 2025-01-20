package com.example.acmarge

import AddTaskTakePhotoDialog
import CustomBottomBar
import NotificationItem
import NotificationScreen
import UserProfileEditScreen
import UserProfileScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.acmarge.ui.navigation.AppNavHost
import com.example.acmarge.ui.theme.ACMArgeTheme
import com.example.acmarge.viewmodel.UserProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ACMArgeTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: UserProfileViewModel) {
    val navController = rememberNavController()

    // Dialog için state (Dialog görünür mü?)
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                onHomeClick = { navController.navigate("home") },
                onFabClick = { showDialog = true }, // Ortadaki FAB tıklanınca Dialog açılır
                onCalendarClick = { navController.navigate("calendar") }
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
                    onNotificationClick = { navController.navigate("notifications") } // Bildirim ekranına git
                )

            }
            composable("notifications") {
                NotificationScreen(
                    notifications = listOf(

                    ),
                    onBackClick = { navController.popBackStack() } // Geri butonuna tıklayınca önceki ekrana dön
                )
            }

            // Profil Ekranı
            composable("profile") {
                UserProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEdit = {
                        navController.navigate("editProfile")
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Profil Düzenleme Ekranı
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
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddTaskTakePhotoDialog(
            onDismiss = { showDialog = false },
            onAddTaskClick = {
                // "Add Task" butonuna basıldığında yapılacaklar
                showDialog = false
                navController.navigate("addTaskScreen")
            },
            onTakePhotoClick = {
                showDialog = false
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
            // Ana ekran içeriği
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