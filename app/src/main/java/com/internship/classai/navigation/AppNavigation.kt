package com.internship.classai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.internship.classai.ui.admin.AddNewUserScreen
import com.internship.classai.ui.admin.AdminDirectoryScreen
import com.internship.classai.ui.admin.AdminScreen
import com.internship.classai.ui.admin.EmployeeDirectoryScreen
import com.internship.classai.ui.login.LoginScreen
import com.internship.classai.ui.login.LoginSession
import com.internship.classai.ui.login.LoginTypeScreen
import com.internship.classai.ui.payments.OfflinePaymentScreen
import com.internship.classai.ui.payments.PaymentDetailsScreen
import com.internship.classai.ui.payments.PaymentSuccessScreen
import com.internship.classai.ui.payments.PaymentViewModel
import com.internship.classai.ui.payments.ReceiptPreviewScreen
import com.internship.classai.ui.settings.AboutClassAIScreen
import com.internship.classai.ui.settings.ChangePasswordScreen
import com.internship.classai.ui.settings.HelpSupportScreen
import com.internship.classai.ui.settings.ProfileScreen
import com.internship.classai.ui.settings.SettingsScreen
import com.internship.classai.ui.students.StudentsScreen
import androidx.compose.ui.platform.LocalContext
import com.internship.classai.ui.admin.AdminUserStore

object Routes {

    const val LOGIN_TYPE = "login_type"
    const val LOGIN = "login"

    const val PAYMENTS = "payments"
    const val PAY_NOW = "pay_now"
    const val WAIVER = "waiver"
    const val PAYMENT_SUCCESS = "payment_success"

    const val STUDENTS = "students"

    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val CHANGE_PASSWORD = "change_password"
    const val ABOUT_CLASS_AI = "about_class_ai"
    const val HELP_SUPPORT = "help_support"

    const val PAYMENT_DETAILS = "payment_details"
    const val RECEIPT_PREVIEW = "receipt_preview"

    const val ADMIN = "admin"
    const val EMPLOYEE_DIRECTORY = "employee_directory"
    const val ADMIN_DIRECTORY = "admin_directory"
    const val ADD_NEW_USER = "add_new_user"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current
    AdminUserStore.initialize(context)

    val session = LoginSession(context)

    val startDestination = when {
        !session.isLoggedIn() -> Routes.LOGIN_TYPE
        session.isAdmin() -> Routes.ADMIN
        else -> Routes.STUDENTS
    }

    val paymentViewModel: PaymentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.LOGIN_TYPE) {
            LoginTypeScreen(
                navController = navController
            )
        }

        composable(
            route = "${Routes.LOGIN}?type={type}",
            arguments = listOf(
                navArgument("type") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            LoginScreen(
                navController = navController,
                backStackEntry = backStackEntry
            )
        }

        // ==============================
        // PAYMENT FLOW
        // ==============================

        composable(Routes.PAYMENTS) {

            OfflinePaymentScreen(
                navController = navController,
                paymentViewModel = paymentViewModel
            )
        }

        composable(Routes.PAYMENT_DETAILS) {

            val student = paymentViewModel.selectedStudent

            if (
                student != null &&
                paymentViewModel.selectedDues.isNotEmpty()
            ) {
                PaymentDetailsScreen(
                    navController = navController,
                    student = student,
                    paymentViewModel = paymentViewModel
                )
            }
        }

        composable(Routes.PAYMENT_SUCCESS) {

            PaymentSuccessScreen(
                navController = navController
            )
        }

        composable(Routes.RECEIPT_PREVIEW) {

            ReceiptPreviewScreen(
                navController = navController,
                paymentViewModel = paymentViewModel
            )
        }

        composable(Routes.STUDENTS) {

            StudentsScreen(
                navController = navController,
                paymentViewModel = paymentViewModel
            )
        }

        // ==============================
        // SETTINGS
        // ==============================

        composable(Routes.SETTINGS) {

            SettingsScreen(
                navController = navController
            )
        }

        composable(Routes.PROFILE) {

            ProfileScreen(
                navController = navController
            )
        }

        composable(Routes.CHANGE_PASSWORD) {

            ChangePasswordScreen(
                navController = navController
            )
        }

        composable(Routes.ABOUT_CLASS_AI) {

            AboutClassAIScreen(
                navController = navController
            )
        }

        composable(Routes.HELP_SUPPORT) {

            HelpSupportScreen(
                navController = navController
            )
        }

        // ==============================
        // ADMIN
        // ==============================

        composable(Routes.ADMIN) {

            AdminScreen(
                navController = navController
            )
        }

        composable(Routes.EMPLOYEE_DIRECTORY) {

            EmployeeDirectoryScreen(
                navController = navController
            )
        }

        composable(Routes.ADMIN_DIRECTORY) {

            AdminDirectoryScreen(
                navController = navController
            )
        }

        composable(Routes.ADD_NEW_USER) {

            AddNewUserScreen(
                navController = navController
            )
        }
    }
}