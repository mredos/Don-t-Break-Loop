import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.acmarge.ui.screens.HomeManagementScreen
import com.example.acmarge.ui.screens.TaskManagementScreen
import com.example.acmarge.ui.screens.TaskSelectionDialog
import com.example.acmarge.ui.screens.getDateList

@OptIn(ExperimentalMaterial3Api::class)
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
    var showDialog by remember { mutableStateOf(false) }

    // Geri tuşuna basıldığında ana sayfaya dön
    BackHandler {
        if (navController.currentDestination?.route != "home") {
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    androidx.compose.material3.Text("BreakLoop", color = Color.White)
                },
                actions = {
                    // Bildirim simgesi
                    androidx.compose.material3.IconButton(onClick = { navController.navigate("notifications") }) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White // İkon rengi beyaz
                        )
                    }
                    // Profil simgesi
                    androidx.compose.material3.IconButton(onClick = { navController.navigate("profile") }) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White // İkon rengi beyaz
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0), // Arka plan rengi (Mavi)
                    titleContentColor = Color.White // Başlık metni rengi
                )
            )
        },
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
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFF1565C0)) // Mavi arka plan rengi
        ) {
            // Ana Sayfa
            composable("home") {
                HomeManagementScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1565C0)), // Mavi arka plan
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> onSelectedDateChange(newDate) },
                    onCameraRequest = onCameraRequest,
                    completedTasks = completedTasks,
                    onCompletedTasksChange = onCompletedTasksChange
                )
            }

            // Bildirimler
            composable("notifications") {
                NotificationScreen(
                    notifications = listOf(),
                    onBackClick = { navController.navigate("home") }
                )
            }

            // Profil
            composable("profile") {
                UserProfileScreen(
                    viewModel = viewModel,
                    onNavigateToEdit = { navController.navigate("editProfile") },
                    onNavigateBack = { navController.navigate("home") }
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

            // Takvim
            composable("calendar") {
                TaskManagementScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1565C0)), // Mavi arka plan
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> onSelectedDateChange(newDate) },
                    onCameraRequest = { onCameraRequest() },
                    completedTasks = completedTasks,
                    onCompletedTasksChange = onCompletedTasksChange
                )
            }

            // 1) Kamera rotası:
            composable("cameraScreen") {
                // Bu ekrana gelindiğinde doğrudan kamera izni iste
                // ve sonra geri dön. Bunu LaunchedEffect ile yapabilirsiniz.
                LaunchedEffect(Unit) {
                    onCameraRequest()
                    // İşiniz bitince geri
                    navController.popBackStack()
                }
            }

            // 2) Görev ekleme rotası:
            composable("addTaskScreen") {
                // Burada yalnızca TaskSelectionDialog veya
                // full-screen bir sayfa şeklinde gösterebilirsiniz.
                AddTaskScreen(
                    tasks = tasks,
                    onTasksChange = { newMap ->
                        tasks.clear()
                        tasks.putAll(newMap)
                    },
                    dateList = getDateList(),
                    onDismiss = { navController.popBackStack() }
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

@Composable
fun AddTaskScreen(
    tasks: MutableMap<String, MutableList<String>>,
    onTasksChange: (MutableMap<String, MutableList<String>>) -> Unit,
    dateList: List<String>,
    onDismiss: () -> Unit
) {
    // Ekran açılır açılmaz TaskSelectionDialog gösterelim
    var showTaskSelectionDialog by remember { mutableStateOf(true) }

    if (showTaskSelectionDialog) {
        TaskSelectionDialog(
            onDismiss = {
                // Diyalog kapatılırsa ekrandan geri dön
                showTaskSelectionDialog = false
                onDismiss()
            },
            onTaskSelected = { selectedTask, selectedTime, selectedDays ->
                // Burada seçilen günlere göre tasks map’ini güncelliyoruz
                val updatedMap = tasks.toMutableMap()
                selectedDays.forEach { date ->
                    val taskWithTime = "$selectedTask at $selectedTime"
                    val taskList = updatedMap[date] ?: mutableListOf()
                    taskList.add(taskWithTime)
                    updatedMap[date] = taskList
                }
                // Değişikliği geri bildirelim
                onTasksChange(updatedMap)

                // Diyalog kapatılınca ekrandan geri dön
                showTaskSelectionDialog = false
                onDismiss()
            },
            dateList = dateList
        )
    }
}
