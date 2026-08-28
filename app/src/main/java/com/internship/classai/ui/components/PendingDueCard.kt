package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.ui.theme.AppColors
import androidx.compose.foundation.layout.height

@Composable
fun PendingDueCard(

    month: String,
    dueDate: String,
    payableAmount: String,
    penalty: String,
    waiver: String,
    totalAmount: String

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        border = BorderStroke(
            1.dp,
            AppColors.Border
        ),

        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        )

    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = month,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            PaymentRow(
                title = "Due Date",
                value = dueDate
            )

            PaymentRow(
                title = "Payable Amount",
                value = payableAmount
            )

            PaymentRow(
                title = "Penalty",
                value = penalty
            )

            PaymentRow(
                title = "Waiver",
                value = waiver
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp
                ),
                color = AppColors.Border
            )

            PaymentRow(
                title = "Total Payable",
                value = totalAmount,
                isBold = true
            )

        }

    }

}

@Composable
private fun PaymentRow(

    title: String,
    value: String,
    isBold: Boolean = false

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Text(
            text = title,
            fontSize = 15.sp,
            color = AppColors.TextSecondary
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight =
                if (isBold)
                    FontWeight.Bold
                else
                    FontWeight.Medium,
            color = AppColors.TextPrimary
        )

    }

}