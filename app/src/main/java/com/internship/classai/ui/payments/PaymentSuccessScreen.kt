package com.internship.classai.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.theme.AppColors

@Composable
fun PaymentSuccessScreen(

    navController: NavHostController

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Card(

            shape = CircleShape,

            colors = CardDefaults.cardColors(

                containerColor = AppColors.PrimaryEnd

            )

        ) {

            Icon(

                imageVector = Icons.Rounded.CheckCircle,

                contentDescription = null,

                tint = AppColors.White,

                modifier = Modifier
                    .padding(24.dp)

            )

        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(

            text = "Payment Successful",

            style = MaterialTheme.typography.headlineSmall

        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(

            text = "The payment has been recorded successfully.",

            fontSize = 16.sp,

            color = AppColors.TextSecondary

        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(

            modifier = Modifier.fillMaxWidth(),

            onClick = {

                navController.navigate(
                    Routes.RECEIPT_PREVIEW
                )

            }

        ) {

            Text(
                "View Receipt"
            )

        }

        Button(

            onClick = {

                navController.navigate(Routes.STUDENTS) {

                    popUpTo(Routes.STUDENTS) {

                        inclusive = true

                    }

                }

            },

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(

                containerColor = AppColors.PrimaryEnd

            )

        ) {

            Text(
                text = "Done"
            )

        }

    }

}