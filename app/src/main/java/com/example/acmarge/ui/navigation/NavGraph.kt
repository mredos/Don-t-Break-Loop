package com.example.acmarge.ui.navigation

import MainHome
import UserProfileScreen
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acmarge.models.VerifyMode
import com.example.acmarge.ui.screens.*
import java.nio.file.WatchEvent

@Composable
fun AppNavHost(
    onRequestCameraPermission: () -> Unit,

    // selectedDate parametresi
    selectedDate: String,
    onSelectedDateChange: (String) -> Unit,

    // tasks
    tasks: MutableMap<String, MutableList<String>>,
    onTasksChange: (MutableMap<String, MutableList<String>>) -> Unit,

    // completedTasks
    completedTasks: MutableMap<String, MutableList<String>>,
    onCompletedTasksChange: (MutableMap<String, MutableList<String>>) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // HomeScreen composable
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // LoginScreen composable
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToPhoneEntrance = { navController.navigate(Screen.EmailVerify.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.MainHome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // RegisterScreen composable
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToPhoneEntrance = { userData ->
                    navController.navigate(
                        "${Screen.PhoneEntrance.route}?fullName=${userData.fullName}&email=${userData.email}&password=${userData.password}"
                    )
                }
            )
        }

        // EmailVerifyScreen composable
        composable(Screen.EmailVerify.route) {
            EmailVerifyScreen(
                onNavigateBack = { navController.popBackStack() },
                onEmailSent = {
                    navController.navigate(Screen.ResetPassword.route) {
                        popUpTo(Screen.EmailVerify.route) { inclusive = true }
                    }
                }
            )
        }

        // PhoneEntranceScreen composable
        composable(
            route = "${Screen.PhoneEntrance.route}?fullName={fullName}&email={email}&password={password}",
            arguments = listOf(
                navArgument("fullName") { type = NavType.StringType; defaultValue = "" },
                navArgument("email") { type = NavType.StringType; defaultValue = "" },
                navArgument("password") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val fullName = backStackEntry.arguments?.getString("fullName") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""

            val userData = UserRegistrationData(fullName, email, password)

            PhoneEntranceScreen(
                userData = userData,
                onNavigateToVerify = { userData, phoneNumber, verificationId ->
                    navController.navigate(
                        "${Screen.Verify.route}/Register/$phoneNumber/$verificationId?fullName=${userData.fullName}&email=${userData.email}&password=${userData.password}"
                    )
                }
            )
        }

        // VerifyScreen composable
        composable(
            route = "${Screen.Verify.route}/{mode}/{phoneNumber}/{verificationId}?fullName={fullName}&email={email}&password={password}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("verificationId") { type = NavType.StringType },
                navArgument("fullName") { type = NavType.StringType; defaultValue = "" },
                navArgument("email") { type = NavType.StringType; defaultValue = "" },
                navArgument("password") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Register"
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
            val fullName = backStackEntry.arguments?.getString("fullName") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""

            val userData = if (mode == VerifyMode.Register.name) {
                UserRegistrationData(fullName, email, password)
            } else null

            VerifyScreen(
                userData = userData,
                phoneNumber = phoneNumber,
                verificationId = verificationId,
                mode = VerifyMode.valueOf(mode),
                onNavigateBack = { navController.popBackStack() },
                onVerificationSuccess = {
                    when (VerifyMode.valueOf(mode)) {
                        VerifyMode.Register -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                        VerifyMode.ResetPassword -> {
                            navController.navigate(Screen.ResetPassword.route) {
                                popUpTo(Screen.Verify.route) { inclusive = true }
                            }
                        }
                    }
                },
                onSendAgain = {
                }
            )
        }

        // ResetPasswordScreen composable
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onResetConfirmed = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MainHome.route) {
            MainHome(
                viewModel = viewModel(),

                // Burada da aynı parametreleri geçiyoruz:
                selectedDate = selectedDate,
                onSelectedDateChange = onSelectedDateChange,

                tasks = tasks,
                completedTasks = completedTasks,
                onCompletedTasksChange = onCompletedTasksChange,

                onCameraRequest = onRequestCameraPermission
            )
        }

        // UserProfileScreen composable
        composable(Screen.Profile.route) {
            UserProfileScreen(
                viewModel = viewModel(),
                onNavigateToEdit = { navController.navigate(Screen.MainHome.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        }
}



