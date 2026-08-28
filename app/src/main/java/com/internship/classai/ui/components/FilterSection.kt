package com.internship.classai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internship.classai.data.model.ClassItem
import com.internship.classai.data.model.SectionItem
import com.internship.classai.ui.theme.AppColors

@Composable
fun FilterSection(

    classes: List<ClassItem>,
    sections: List<SectionItem>,

    selectedClass: ClassItem?,
    selectedSection: SectionItem?,

    onClassSelected: (ClassItem) -> Unit,
    onSectionSelected: (SectionItem) -> Unit

) {

    var classExpanded by remember {
        mutableStateOf(false)
    }

    var sectionExpanded by remember {
        mutableStateOf(false)
    }

    /*
     * Section must always depend on the selected class.
     *
     * We also remove duplicate section names so the
     * dropdown does not show repeated DAY / MORNING etc.
     */
    val availableSections = remember(
        sections,
        selectedClass
    ) {

        selectedClass
            ?.let { currentClass ->

                sections
                    .filter { section ->
                        section.classId == currentClass.id
                    }
                    .distinctBy { section ->
                        section.name.trim().uppercase()
                    }

            }
            ?: emptyList()
    }

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        horizontalArrangement =
            Arrangement.spacedBy(12.dp)

    ) {

        // ==================================================
        // CLASS
        // ==================================================

        DropdownCard(

            modifier =
                Modifier.weight(1f),

            title = "Class *",

            value =
                selectedClass?.name
                    ?: "Select Class",

            expanded =
                classExpanded,

            onExpand = {
                classExpanded = true
            },

            onDismiss = {
                classExpanded = false
            }

        ) {

            classes.forEach { item ->

                DropdownMenuItem(

                    text = {
                        Text(item.name)
                    },

                    onClick = {

                        classExpanded = false

                        onClassSelected(item)

                    }

                )

            }

        }

        // ==================================================
        // SECTION
        // ==================================================

        DropdownCard(

            modifier =
                Modifier.weight(1f),

            title = "Section",

            value =
                selectedSection?.name
                    ?: if (selectedClass == null) {
                        "Select Class First"
                    } else {
                        "Select Section"
                    },

            expanded =
                sectionExpanded &&
                        selectedClass != null,

            onExpand = {

                /*
                 * Do nothing until a class has been selected.
                 */
                if (selectedClass != null) {

                    sectionExpanded = true

                }

            },

            onDismiss = {

                sectionExpanded = false

            },

            enabled =
                selectedClass != null

        ) {

            availableSections.forEach { item ->

                DropdownMenuItem(

                    text = {
                        Text(item.name)
                    },

                    onClick = {

                        sectionExpanded = false

                        onSectionSelected(item)

                    }

                )

            }

        }

    }

}

@Composable
private fun DropdownCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String,

    expanded: Boolean,

    onExpand: () -> Unit,

    onDismiss: () -> Unit,

    enabled: Boolean = true,

    content: @Composable () -> Unit

) {

    Column(
        modifier = modifier
    ) {

        Text(

            text = title,

            fontSize = 12.sp,

            fontWeight =
                FontWeight.Medium,

            color =
                AppColors.TextSecondary

        )

        Box {

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(54.dp)
                    .then(

                        if (enabled) {

                            Modifier.clickable {
                                onExpand()
                            }

                        } else {

                            Modifier

                        }

                    ),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            AppColors.Surface

                    ),

                border =
                    BorderStroke(

                        1.dp,

                        AppColors.Border

                    )

            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(
                            horizontal = 14.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Text(

                        text = value,

                        modifier =
                            Modifier.weight(1f),

                        fontSize = 14.sp,

                        color =
                            if (enabled) {
                                AppColors.TextPrimary
                            } else {
                                AppColors.TextSecondary
                            }

                    )

                    Icon(

                        imageVector =
                            Icons.Outlined
                                .KeyboardArrowDown,

                        contentDescription = null,

                        tint =
                            if (enabled) {
                                AppColors.TextSecondary
                            } else {
                                AppColors.Border
                            }

                    )

                }

            }

            DropdownMenu(

                expanded = expanded,

                onDismissRequest =
                    onDismiss

            ) {

                content()

            }

        }

    }

}