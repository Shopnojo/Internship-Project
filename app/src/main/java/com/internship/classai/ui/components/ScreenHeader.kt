package com.internship.classai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.ui.theme.AppColors

@Composable
fun ScreenHeader(

    title: String,

    subtitle: String

) {

    Column(

        modifier = Modifier.padding(

            horizontal = 16.dp,

            vertical = 16.dp

        )

    ) {

        Text(

            text = title,

            fontSize = 28.sp,

            fontWeight = FontWeight.Bold,

            color = AppColors.TextPrimary

        )

        Text(

            text = subtitle,

            fontSize = 14.sp,

            color = AppColors.TextSecondary

        )

    }

}