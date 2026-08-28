package com.internship.classai.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.FieldLabel
import com.internship.classai.ui.components.PendingDueCard
import com.internship.classai.ui.components.PendingDueTile
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.components.StudentCard
import com.internship.classai.ui.theme.AppColors

@Composable
fun OfflinePaymentScreen(

    navController: NavHostController,

    paymentViewModel: PaymentViewModel

) {

    Scaffold(

        topBar = {

            AppToolbar(
                onMenuClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )

        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())

        ) {

            ScreenHeader(

                title = "Offline Payment",

                subtitle = "Collect payments and manage waivers"

            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            //==================================================
            // SELECTED STUDENT
            //==================================================

            paymentViewModel.selectedStudent?.let { student ->

                StudentCard(
                    student = student
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                //==================================================
                // PENDING DUES
                //==================================================

                FieldLabel(
                    "Pending Dues"
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                if (paymentViewModel.selectedStudentDues.isEmpty()) {

                    Text(

                        text = "No pending dues for the selected student.",

                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),

                        color = AppColors.TextSecondary

                    )

                } else {

                    paymentViewModel.selectedStudentDues
                        .sortedBy { it.dueDate }
                        .forEach { due ->

                            PendingDueTile(

                                month = due.month,

                                dueDate = due.dueDate,

                                amount = "₹${due.totalAmount}",

                                selected =
                                    paymentViewModel.selectedDues.contains(due),

                                onCheckedChange = {

                                    paymentViewModel.toggleDueSelection(due)

                                },

                                onDetailsClick = {

                                    paymentViewModel.selectDue(due)

                                }

                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                        }


                    //==================================================
                    // SELECTION SUMMARY
                    //==================================================

                    if (paymentViewModel.selectedDues.isNotEmpty()) {

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        Text(

                            text =
                                "${paymentViewModel.selectedDues.size} item(s) selected",

                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            ),

                            color = AppColors.TextSecondary

                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(

                            text =
                                "Total Payable: ₹${
                                    paymentViewModel.selectedDues.sumOf {
                                        it.totalAmount
                                    }
                                }",

                            modifier = Modifier.padding(
                                horizontal = 16.dp
                            ),

                            color = AppColors.TextPrimary

                        )

                    }


                    //==================================================
                    // COLLECT PAYMENT
                    //==================================================

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Button(

                        onClick = {

                            navController.navigate(
                                Routes.PAYMENT_DETAILS
                            )

                        },

                        enabled =
                            paymentViewModel.selectedDues.isNotEmpty(),

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),

                        colors = ButtonDefaults.buttonColors(

                            containerColor =
                                AppColors.PrimaryEnd,

                            contentColor =
                                AppColors.White,

                            disabledContainerColor =
                                AppColors.Border,

                            disabledContentColor =
                                AppColors.TextSecondary

                        )

                    ) {

                        Text(
                            text = "Collect Payment"
                        )

                    }

                }

            }

        }

    }


    //==================================================
    // DUE DETAILS POPUP
    //==================================================

    paymentViewModel.selectedDue?.let { due ->

        AlertDialog(

            onDismissRequest = {

                paymentViewModel.selectDue(null)

            },

            title = {

                Text(
                    text = "Due Details"
                )

            },

            text = {

                PendingDueCard(

                    month = due.month,

                    dueDate = due.dueDate,

                    payableAmount = "₹${due.payableAmount}",

                    penalty = "₹${due.penalty}",

                    waiver = "₹${due.waiver}",

                    totalAmount = "₹${due.totalAmount}"

                )

            },

            confirmButton = {

                Button(

                    onClick = {

                        paymentViewModel.selectDue(null)

                    },

                    colors = ButtonDefaults.buttonColors(

                        containerColor =
                            AppColors.PrimaryEnd,

                        contentColor =
                            AppColors.White

                    )

                ) {

                    Text(
                        text = "Close"
                    )

                }

            }

        )

    }

}