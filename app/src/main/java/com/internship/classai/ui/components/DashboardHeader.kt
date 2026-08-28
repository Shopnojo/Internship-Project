package com.internship.classai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.ui.theme.AppColors

@Composable
fun DashboardHeader(
    username: String
) {

    Column {

        Text(
            text = "Good Morning",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))

        Text(
            text = "$username 👋",
            fontSize = 22.sp,
            color = AppColors.TextSecondary
        )

    }

}