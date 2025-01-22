import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.acmarge.viewmodel.UserProfileViewModel
import com.example.acmarge.ui.components.CustomBottomBar


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.acmarge.ui.screens.HomeManagementScreen
import com.example.acmarge.ui.screens.TaskManagementScreen
import com.example.acmarge.ui.screens.getDateList

@Composable
fun MainHome(
    viewModel: UserProfileViewModel,

    // Seçili tarih
    selectedDate: String,
    onSelectedDateChange: (String) -> Unit,

    // Görevler
    tasks: MutableMap<String, MutableList<String>>,
    completedTasks: MutableMap<String, MutableList<String>>,
    onCompletedTasksChange: (MutableMap<String, MutableList<String>>) -> Unit,

    // Kamera isteği
    onCameraRequest: () -> Unit
) {
    val navController = rememberNavController()
    var selectedDate by remember { mutableStateOf(getDateList()[30]) }
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
                HomeManagementScreen(
                    modifier = Modifier.fillMaxSize(),
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> selectedDate = newDate },
                    onCameraRequest = onCameraRequest,
                    completedTasks = completedTasks,
                    onCompletedTasksChange = onCompletedTasksChange
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
            composable("calendar") {
                TaskManagementScreen(
                    modifier = Modifier.fillMaxSize(),
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> selectedDate = newDate },
                    onCameraRequest = { onCameraRequest()},
                    completedTasks = completedTasks,
                    onCompletedTasksChange = onCompletedTasksChange
                )
            }
        }
    }

    // Eğer `showDialog` true ise dialog göster
    if (showDialog) {
        AddTaskTakePhotoDialog(
            onDismiss = { showDialog = false },
            onAddTaskClick = {
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