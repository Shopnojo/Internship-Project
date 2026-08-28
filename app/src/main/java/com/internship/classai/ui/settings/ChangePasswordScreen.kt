package com.internship.classai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.theme.AppColors

@Composable
fun ChangePasswordScreen(
    navController: NavHostController
) {

    var currentPassword by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var currentPasswordVisible by remember {
        mutableStateOf(false)
    }

    var newPasswordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    Scaffold(

        topBar = {

            AppToolbar(
                onMenuClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )

        }

    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .padding(20.dp)

        ) {

            Text(
                text = "Change Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Update your account password",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            PasswordField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = ""
                },
                label = "Current Password",
                visible = currentPasswordVisible,
                onVisibilityChanged = {
                    currentPasswordVisible = !currentPasswordVisible
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            PasswordField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                label = "New Password",
                visible = newPasswordVisible,
                onVisibilityChanged = {
                    newPasswordVisible = !newPasswordVisible
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = "Confirm New Password",
                visible = confirmPasswordVisible,
                onVisibilityChanged = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }
            )

            if (errorMessage.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = errorMessage,
                    fontSize = 13.sp,
                    color = AppColors.TextSecondary
                )

            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Button(

                onClick = {

                    errorMessage = when {

                        currentPassword.isBlank() ->
                            "Please enter your current password."

                        newPassword.isBlank() ->
                            "Please enter a new password."

                        confirmPassword.isBlank() ->
                            "Please confirm your new password."

                        newPassword != confirmPassword ->
                            "New passwords do not match."

                        else ->
                            "Password update will be available after account integration."

                    }

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(14.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryEnd
                )

            ) {

                Text(
                    text = "Update Password",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.White
                )

            }

        }

    }

}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onVisibilityChanged: () -> Unit
) {

    OutlinedTextField(

        value = value,

        onValueChange = onValueChange,

        modifier = Modifier.fillMaxWidth(),

        label = {
            Text(text = label)
        },

        leadingIcon = {

            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = AppColors.TextSecondary
            )

        },

        trailingIcon = {

            IconButton(
                onClick = onVisibilityChanged
            ) {

                Icon(
                    imageVector = if (visible)
                        Icons.Outlined.VisibilityOff
                    else
                        Icons.Outlined.Visibility,
                    contentDescription = if (visible)
                        "Hide password"
                    else
                        "Show password",
                    tint = AppColors.TextSecondary
                )

            }

        },

        singleLine = true,

        visualTransformation = if (visible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),

        shape = RoundedCornerShape(14.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = AppColors.Surface,

            unfocusedContainerColor = AppColors.Surface,

            focusedBorderColor = AppColors.Border,

            unfocusedBorderColor = AppColors.Border,

            focusedLabelColor = AppColors.PrimaryEnd,

            cursorColor = AppColors.PrimaryEnd

        )

    )

}