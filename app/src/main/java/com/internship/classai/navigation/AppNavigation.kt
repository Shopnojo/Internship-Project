package com.internship.classai.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.internship.classai.ui.login.LoginScreen
import com.internship.classai.ui.login.LoginTypeScreen
import com.internship.classai.ui.payments.OfflinePaymentScreen
import com.internship.classai.ui.payments.PaymentDetailsScreen
import com.internship.classai.ui.payments.PaymentSuccessScreen
import com.internship.classai.ui.payments.PaymentViewModel
import com.internship.classai.ui.payments.ReceiptPreviewScreen
import com.internship.classai.ui.students.StudentsScreen
import com.internship.classai.ui.settings.SettingsScreen
import com.internship.classai.ui.settings.AboutClassAIScreen
import com.internship.classai.ui.settings.HelpSupportScreen
import com.internship.classai.ui.settings.ProfileScreen
import com.internship.classai.ui.settings.ChangePasswordScreen

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
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val paymentViewModel: PaymentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN_TYPE
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

        composable(Routes.PAYMENTS) {

            OfflinePaymentScreen(
                navController = navController,
                paymentViewModel = paymentViewModel
            )
        }

        composable(Routes.PAYMENT_DETAILS) {

            val student = paymentViewModel.selectedStudent

            if (student != null && paymentViewModel.selectedDues.isNotEmpty()) {

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

        composable(Routes.SETTINGS) {

            SettingsScreen(
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
    }
}