package com.internship.classai.ui.payments

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.ReceiptView
import com.internship.classai.ui.theme.AppColors
import com.internship.classai.utils.ReceiptPrintManager
import com.internship.classai.utils.ReceiptShareManager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptPreviewScreen(

    navController: NavHostController,

    paymentViewModel: PaymentViewModel

) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    // Payment history has been removed.
    // The ViewModel keeps only the latest payment.
    val payment =
        paymentViewModel.lastPayment

    // Tracks whether this receipt has been
    // successfully printed at least once.
    var hasPrinted by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text("Receipt Preview")

                },

                navigationIcon = {

                    IconButton(

                        onClick = {

                            navController.navigate(
                                Routes.STUDENTS
                            ) {

                                popUpTo(
                                    Routes.STUDENTS
                                ) {
                                    inclusive = true
                                }

                            }

                        }

                    ) {

                        Icon(

                            imageVector =
                                Icons.Outlined.ArrowBack,

                            contentDescription = null

                        )

                    }

                },

                colors =
                    TopAppBarDefaults.topAppBarColors(

                        containerColor =
                            AppColors.PrimaryEnd,

                        titleContentColor =
                            AppColors.White,

                        navigationIconContentColor =
                            AppColors.White

                    )

            )

        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )

        ) {

            payment?.let {

                ReceiptView(

                    payment = it

                )

            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {

                // ==================================================
                // PRINT / RE-PRINT
                // ==================================================

                Button(

                    modifier =
                        Modifier.weight(1f),

                    onClick = {

                        payment?.let {

                            scope.launch {

                                val success =
                                    ReceiptPrintManager.printReceipt(

                                        context = context,

                                        payment = it

                                    )

                                if (success) {
                                    hasPrinted = true
                                }

                                Toast.makeText(
                                    context,
                                    if (success) {
                                        "Printing..."
                                    } else {
                                        "Unable to print receipt."
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }

                    }

                ) {

                    Icon(

                        imageVector =
                            Icons.Outlined.Print,

                        contentDescription = null

                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        if (hasPrinted) {
                            "Re-print"
                        } else {
                            "Print"
                        }
                    )

                }

                // ==================================================
                // SHARE
                // ==================================================

                Button(

                    modifier =
                        Modifier.weight(1f),

                    onClick = {

                        payment?.let {

                            val success =
                                ReceiptShareManager.shareReceipt(

                                    context = context,

                                    payment = it

                                )

                            if (!success) {

                                Toast.makeText(

                                    context,

                                    "Unable to share receipt.",

                                    Toast.LENGTH_SHORT

                                ).show()

                            }

                        }

                    }

                ) {

                    Icon(

                        imageVector =
                            Icons.Outlined.Share,

                        contentDescription = null

                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text("Share")

                }

            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

        }

    }

}