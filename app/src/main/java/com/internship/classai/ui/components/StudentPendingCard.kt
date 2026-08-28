package com.internship.classai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.data.model.Student
import com.internship.classai.ui.theme.AppColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment

@Composable
fun StudentPendingCard(

    student: Student,

    pendingCount: Int,

    totalDue: Int,

    onClick: () -> Unit

) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),

        colors = CardDefaults.cardColors(

            containerColor = AppColors.Surface

        )

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ){

            Text(

                text = student.name,

                fontWeight = FontWeight.Bold,

                fontSize = 18.sp,

                color = AppColors.TextPrimary

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = student.studentId,

                color = AppColors.TextSecondary

            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.Bottom

            ) {

                Column {

                    Text(

                        text = "$pendingCount Pending Due(s)",

                        color = AppColors.TextSecondary

                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(

                        text = "₹$totalDue",

                        fontWeight = FontWeight.Bold,

                        fontSize = 20.sp,

                        color = AppColors.PrimaryEnd

                    )

                }

                Button(

                    onClick = onClick,

                    colors = ButtonDefaults.buttonColors(

                        containerColor = AppColors.PrimaryEnd,

                        contentColor = AppColors.White

                    )

                ) {

                    Text("View")

                }

            }

        }

    }

}