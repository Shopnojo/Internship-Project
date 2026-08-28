package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import com.internship.classai.ui.theme.AppColors

@Composable
fun StudentCard(
    student: Student?,
    classes: List<ClassItem> = emptyList(),
    sections: List<SectionItem> = emptyList(),
    onClick: () -> Unit = {}
) {

    if (student == null) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppColors.Surface
            ),
            border = BorderStroke(
                1.dp,
                AppColors.Border
            )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "No Student Selected",
                    color = AppColors.TextSecondary
                )

            }

        }

        return
    }

    val className =
        classes.firstOrNull {
            it.id == student.classId
        }?.name ?: ""

    val sectionName =
        sections.firstOrNull {
            it.id == student.sectionId
        }?.name ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onClick()
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ==================================================
            // STUDENT PROFILE IMAGE
            // ==================================================

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(AppColors.Background),
                contentAlignment = Alignment.Center
            ) {

                if (!student.image.isNullOrBlank()) {

                    AsyncImage(
                        model = "http://192.168.29.208:8000/${student.image}",
                        contentDescription = "Student profile image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = AppColors.PrimaryEnd
                    )

                }

            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = student.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = student.studentId,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary
                )

            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = className,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = sectionName,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary
                )

            }

        }

    }

}