package com.internship.classai.ui.login

import android.app.Activity
import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.internship.classai.R
import com.internship.classai.data.model.LoginRequest
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    SideEffect {
        val activity = context as? Activity

        activity?.window?.let { window ->
            window.statusBarColor = AndroidColor.WHITE

            WindowCompat.getInsetsController(
                window,
                window.decorView
            ).isAppearanceLightStatusBars = true
        }
    }

    val loginType = backStackEntry
        .arguments
        ?.getString("type")
        ?: "employee"

    val isAdmin = loginType == "admin"

    val title = if (isAdmin) {
        "Sign in as Admin"
    } else {
        "Sign in as Employee"
    }

    var userId by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val canSignIn =
        userId.isNotBlank() &&
                password.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .padding(
                horizontal = 32.dp,
                vertical = 24.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.classai_logo
            ),
            contentDescription = "ClassAI",
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.height(36.dp)
        )

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        OutlinedTextField(
            value = userId,
            onValueChange = {
                userId = it
            },
            label = {
                Text("User ID")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    try {
                        val role = if (isAdmin) {
                            "admin"
                        } else {
                            "employee"
                        }

                        val response = RetrofitClient.apiService.login(
                            LoginRequest(
                                userId = userId.trim(),
                                password = password,
                                role = role
                            )
                        )

                        if (response.success) {
                            val session = LoginSession(context)

                            session.login(
                                userId = response.userId ?: userId.trim(),
                                role = response.role ?: role
                            )

                            if (isAdmin) {
                                navController.navigate(Routes.ADMIN) {
                                    popUpTo(Routes.LOGIN_TYPE) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                navController.navigate(Routes.STUDENTS) {
                                    popUpTo(Routes.LOGIN_TYPE) {
                                        inclusive = true
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Unable to sign in. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            enabled = canSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryEnd,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "SIGN IN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}