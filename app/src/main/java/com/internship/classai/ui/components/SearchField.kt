package com.internship.classai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.internship.classai.ui.theme.AppColors

@Composable
fun SearchField(

    searchQuery: String,

    onSearchQueryChanged: (String) -> Unit,

    onClearSearch: () -> Unit,

    onFocusChanged: (Boolean) -> Unit,

    onSearch: () -> Unit,

    placeholder: String = "Enter Student ID"

) {

    OutlinedTextField(

        value = searchQuery,

        onValueChange = onSearchQueryChanged,

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .onFocusChanged {

                onFocusChanged(it.isFocused)

            },

        placeholder = {

            Text(
                text = placeholder,
                color = AppColors.TextSecondary
            )

        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),

        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),

        trailingIcon = {

            if (searchQuery.isNotEmpty()) {

                IconButton(

                    onClick = {
                        onClearSearch()
                    }

                ) {

                    Icon(

                        imageVector = Icons.Default.Close,

                        contentDescription = "Clear Search",

                        tint = AppColors.TextSecondary

                    )

                }

            } else {

                Icon(

                    imageVector = Icons.Outlined.Search,

                    contentDescription = null,

                    tint = AppColors.TextSecondary

                )

            }

        },

        shape = RoundedCornerShape(16.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = AppColors.Surface,

            unfocusedContainerColor = AppColors.Surface,

            focusedBorderColor = AppColors.Border,

            unfocusedBorderColor = AppColors.Border,

            cursorColor = AppColors.PrimaryEnd

        )

    )

}