package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.data.model.Student
import com.internship.classai.ui.theme.AppColors

@Composable
fun StudentDropdown(

    students: List<Student>,

    selectedStudent: Student?,

    onStudentSelected: (Student) -> Unit

) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Students *",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextSecondary
        )

        Box {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(54.dp)
                    .clickable {
                        expanded = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                ),
                border = BorderStroke(
                    1.dp,
                    AppColors.Border
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(

                        text = selectedStudent?.name ?: "Select Student",

                        modifier = Modifier.weight(1f),

                        fontSize = 15.sp,

                        color = if (selectedStudent == null)
                            AppColors.TextSecondary
                        else
                            AppColors.TextPrimary

                    )

                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )

                }

            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {

                students.forEach { student ->

                    DropdownMenuItem(
                        text = {
                            Text(student.name)
                        },
                        onClick = {

                            onStudentSelected(student)
                            expanded = false

                        }
                    )

                }

            }

        }

    }

}