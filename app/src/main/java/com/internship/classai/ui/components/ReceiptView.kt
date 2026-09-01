package com.internship.classai.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.R
import com.internship.classai.data.model.PaymentRecord
import com.internship.classai.ui.theme.AppColors

@Composable
fun ReceiptView(

    payment: PaymentRecord

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(

            containerColor = AppColors.Surface

        )

    ) {

        Column(

            modifier = Modifier.padding(20.dp)

        ) {

            //--------------------------------------------------
            // LOGOS
            //--------------------------------------------------

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Image(

                    painter = painterResource(R.drawable.classai_logo),

                    contentDescription = null,

                    modifier = Modifier.size(56.dp),

                    contentScale = ContentScale.Fit

                )

                Image(

                    painter = painterResource(R.drawable.classai_logo),

                    contentDescription = null,

                    modifier = Modifier.height(42.dp),

                    contentScale = ContentScale.Fit

                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(

                text = "RCC PUBLIC SCHOOL",

                modifier = Modifier.fillMaxWidth(),

                color = AppColors.TextSecondary,

                fontWeight = FontWeight.Bold,

                fontSize = 18.sp

            )

            Text(

                text = "School Address",

                color = AppColors.TextSecondary

            )

            Text(

                text = "Contact Number",

                color = AppColors.TextSecondary

            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // This is the exact transaction_no stored in the DB.
            ReceiptRow(
                "Receipt No.",
                payment.receiptNumber
            )

            ReceiptRow(
                "Date",
                payment.paymentDate
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptRow(
                "Student Name",
                payment.studentName
            )

            ReceiptRow(
                "Student ID",
                payment.studentId.toString()
            )

            ReceiptRow(
                "Class",
                payment.className
            )

            ReceiptRow(
                "Section",
                payment.sectionName
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptRow(
                "Fee Month",
                payment.month
            )

            // Directly from payment_entries.amount
            ReceiptRow(
                "Payable",
                "₹${payment.amountPaid}"
            )

            // Directly from payment_entries.penalty
            ReceiptRow(
                "Penalty",
                "₹${payment.penalty}"
            )

            // Directly from payment_entries.waiver
            ReceiptRow(
                "Waiver",
                "₹${payment.waiver}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(

                text = "TOTAL PAID",

                color = AppColors.TextSecondary,

                fontWeight = FontWeight.Bold,

                fontSize = 16.sp

            )

            // Directly from payment_entries.net_amount.
            // No Android-side calculation.
            Text(

                text = "₹${payment.netAmount}",

                fontWeight = FontWeight.ExtraBold,

                fontSize = 28.sp,

                color = AppColors.PrimaryEnd

            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            ReceiptRow(

                "Payment Method",

                payment.paymentMethod

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(

                text = "Thank You!",

                modifier = Modifier.fillMaxWidth(),

                color = AppColors.TextSecondary,

                fontWeight = FontWeight.Bold

            )

            Text(

                text = "This is a computer generated receipt.",

                color = AppColors.TextSecondary,

                fontSize = 12.sp

            )

        }

    }

}

@Composable
private fun ReceiptRow(

    title: String,

    value: String

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Text(

            text = title,

            color = AppColors.TextPrimary

        )

        Text(

            text = value,

            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary

        )

    }

}