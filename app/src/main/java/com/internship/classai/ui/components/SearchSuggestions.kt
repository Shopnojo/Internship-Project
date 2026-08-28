package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.SectionItem
import com.internship.classai.data.model.Student
import com.internship.classai.ui.theme.AppColors

@Composable
fun SearchSuggestions(

    students: List<Student>,

    classes: List<ClassItem> = emptyList(),

    sections: List<SectionItem> = emptyList(),

    onStudentSelected: (Student) -> Unit

) {

    if (students.isEmpty()) return

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

        LazyColumn(

            modifier = Modifier.heightIn(
                max = 240.dp
            )

        ) {

            items(students) { student ->

                SearchSuggestionItem(

                    student = student,

                    classes = classes,

                    sections = sections,

                    onClick = {

                        onStudentSelected(student)

                    }

                )

            }

        }

    }

}

@Composable
private fun SearchSuggestionItem(

    student: Student,

    classes: List<ClassItem>,

    sections: List<SectionItem>,

    onClick: () -> Unit

) {

    val className =
        classes
            .firstOrNull {
                it.id == student.classId
            }
            ?.name
            ?: ""

    val sectionName =
        sections
            .firstOrNull {
                it.id == student.sectionId
            }
            ?.name
            ?: ""

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()

            }
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )

    ) {

        Text(

            text = student.name,

            fontWeight = FontWeight.Bold,

            fontSize = 15.sp,

            color = AppColors.TextPrimary

        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(

            text =
                "${student.studentId} • $className • $sectionName",

            fontSize = 13.sp,

            color = AppColors.TextSecondary

        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(

            color = AppColors.Border

        )

    }

}