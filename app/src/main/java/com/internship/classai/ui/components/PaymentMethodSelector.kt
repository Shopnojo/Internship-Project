package com.internship.classai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.internship.classai.data.model.PaymentMethod
import com.internship.classai.ui.theme.AppColors

@Composable
fun PaymentMethodSelector(

    selectedMethod: PaymentMethod,

    onMethodSelected: (PaymentMethod) -> Unit,

    isAdvancedSearch: Boolean = true

) {

    FlowRow(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(12.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        PaymentMethod.entries.forEach { method ->

            val enabled =
                isAdvancedSearch || method == PaymentMethod.CASH

            FilterChip(

                selected = selectedMethod == method,

                enabled = enabled,

                onClick = {

                    onMethodSelected(method)

                },

                label = {

                    Text(
                        text = method.name
                    )

                },

                colors = FilterChipDefaults.filterChipColors(

                    selectedContainerColor =
                        AppColors.PrimaryEnd,

                    selectedLabelColor =
                        AppColors.White,

                    disabledContainerColor =
                        AppColors.Background,

                    disabledLabelColor =
                        AppColors.TextSecondary

                )

            )

        }

    }

}