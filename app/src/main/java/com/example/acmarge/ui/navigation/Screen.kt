package com.example.acmarge.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Verify : Screen("verify_screen")
    object ResetPassword : Screen("reset_password_screen")
    object PhoneEntrance : Screen("phone_entrance_screen")
    object EmailVerify : Screen("email_verify_screen")
    object Profile : Screen("profile_screen")
    object EditProfileScreen : Screen("edit_profile_screen")
    object MainHome : Screen("main_home_screen")
    object Notifications : Screen("notifications_screen")


}
