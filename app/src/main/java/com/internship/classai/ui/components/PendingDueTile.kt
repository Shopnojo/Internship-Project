package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.ui.theme.AppColors

@Composable
fun PendingDueTile(

    month: String,

    dueDate: String,

    amount: String,

    selected: Boolean,

    onCheckedChange: (Boolean) -> Unit,

    onDetailsClick: () -> Unit

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

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),

            verticalAlignment = Alignment.CenterVertically

        ) {

            Checkbox(

                checked = selected,

                onCheckedChange = onCheckedChange

            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = month,

                    fontSize = 14.sp,

                    fontWeight = FontWeight.SemiBold,

                    color = AppColors.TextPrimary

                )

                Text(

                    text = dueDate,

                    fontSize = 12.sp,

                    color = AppColors.TextSecondary

                )

            }

            Text(

                text = amount,

                fontSize = 14.sp,

                fontWeight = FontWeight.SemiBold,

                color = AppColors.TextPrimary

            )

            IconButton(

                onClick = onDetailsClick

            ) {

                Icon(

                    imageVector = Icons.Default.Add,

                    contentDescription = "View Due Details",

                    tint = AppColors.TextSecondary

                )

            }

        }

    }

}