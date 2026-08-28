package com.internship.classai.ui.payments

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.data.model.PaymentMethod
import com.internship.classai.data.model.Student
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.PaymentInfoRow
import com.internship.classai.ui.components.PaymentMethodSelector
import com.internship.classai.ui.theme.AppColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailsScreen(
    navController: NavHostController,
    student: Student,
    paymentViewModel: PaymentViewModel
) {

    var paymentMethod by remember {
        mutableStateOf(PaymentMethod.CASH)
    }

    var paymentDate by remember {
        mutableStateOf(
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        navController.context,
        { _, year, month, day ->

            calendar.set(year, month, day)

            paymentDate = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(calendar.time)

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var remarks by remember {
        mutableStateOf("")
    }

    val scope = rememberCoroutineScope()

    val className =
        paymentViewModel.classes.firstOrNull {
            it.id == student.classId
        }?.name ?: ""

    val sectionName =
        paymentViewModel.sections.firstOrNull {
            it.id == student.sectionId
        }?.name ?: ""

    val selectedDues = paymentViewModel.selectedDues

    val totalPayable = selectedDues.sumOf {
        it.totalAmount
    }

    Scaffold(

        containerColor = AppColors.Background,

        topBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppColors.PrimaryStart,
                                AppColors.PrimaryEnd
                            )
                        )
                    )
            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    IconButton(

                        onClick = {
                            navController.popBackStack()
                        }

                    ) {

                        Icon(

                            imageVector = Icons.Outlined.ArrowBack,

                            contentDescription = null,

                            tint = AppColors.White

                        )

                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(

                        text = "Payment Details",

                        color = AppColors.White,

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold

                    )

                }

            }

        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)

        ) {

            //==================================================
            // STUDENT DETAILS
            //==================================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                )

            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = student.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppColors.TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = student.studentId,
                        color = AppColors.TextSecondary
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "$className • $sectionName",
                        color = AppColors.TextSecondary
                    )

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            //==================================================
            // SELECTED DUES
            //==================================================

            selectedDues.forEach { due ->

                Card(

                    modifier = Modifier.fillMaxWidth(),

                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.Surface
                    )

                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = due.month,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppColors.TextPrimary
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        PaymentInfoRow(
                            "Due Date",
                            due.dueDate
                        )

                        PaymentInfoRow(
                            "Payable Amount",
                            "₹${due.payableAmount}"
                        )

                        PaymentInfoRow(
                            "Penalty",
                            "₹${due.penalty}"
                        )

                        PaymentInfoRow(
                            "Waiver",
                            "₹${due.waiver}"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        PaymentInfoRow(
                            "Total",
                            "₹${due.totalAmount}"
                        )

                    }

                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

            }

            //==================================================
            // PAYMENT METHOD
            //==================================================

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            PaymentMethodSelector(

                selectedMethod = paymentMethod,

                onMethodSelected = {
                    paymentMethod = it
                },

                isAdvancedSearch =
                    paymentViewModel.isAdvancedSearch

            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            //==================================================
            // PAYMENT DATE
            //==================================================

            OutlinedTextField(

                value = paymentDate,

                onValueChange = {},

                readOnly = true,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text("Payment Date *")
                },

                trailingIcon = {

                    IconButton(
                        onClick = {
                            datePickerDialog.show()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = AppColors.PrimaryEnd
                        )

                    }

                },

                colors = OutlinedTextFieldDefaults.colors(

                    focusedContainerColor =
                        AppColors.Surface,

                    unfocusedContainerColor =
                        AppColors.Surface,

                    focusedBorderColor =
                        AppColors.Border,

                    unfocusedBorderColor =
                        AppColors.Border,

                    focusedTextColor =
                        AppColors.TextPrimary,

                    unfocusedTextColor =
                        AppColors.TextPrimary

                )

            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            //==================================================
            // REMARKS
            //==================================================

            OutlinedTextField(

                value = remarks,

                onValueChange = {
                    remarks = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),

                label = {
                    Text("Remarks")
                },

                placeholder = {
                    Text("Optional")
                },

                colors = OutlinedTextFieldDefaults.colors(

                    focusedContainerColor =
                        AppColors.Surface,

                    unfocusedContainerColor =
                        AppColors.Surface,

                    focusedBorderColor =
                        AppColors.Border,

                    unfocusedBorderColor =
                        AppColors.Border,

                    focusedTextColor =
                        AppColors.TextPrimary,

                    unfocusedTextColor =
                        AppColors.TextPrimary

                )

            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            //==================================================
            // TOTAL
            //==================================================

            HorizontalDivider(
                color = AppColors.Border
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "Total Payable",
                color = AppColors.TextSecondary,
                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "₹$totalPayable",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            //==================================================
            // COLLECT PAYMENT
            //==================================================

            Button(

                onClick = {

                    scope.launch {

                        val success =
                            paymentViewModel.collectPayment(

                                paymentMethod =
                                    paymentMethod.name,

                                paymentDate =
                                    paymentDate,

                                remarks =
                                    remarks

                            )

                        if (success) {

                            navController.navigate(
                                Routes.RECEIPT_PREVIEW
                            )

                        }

                    }

                },

                enabled = selectedDues.isNotEmpty(),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

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
                    text = "Collect Payment",
                    color = AppColors.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

        }

    }

}