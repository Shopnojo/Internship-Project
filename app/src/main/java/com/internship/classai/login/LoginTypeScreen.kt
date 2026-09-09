package com.internship.classai.ui.login

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.internship.classai.R
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.theme.AppColors

@Composable
fun LoginTypeScreen(
    navController: NavController
) {
    val context = LocalContext.current

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

        /*
         * ClassAI logo
         */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.classai_logo
                    ),
                    contentDescription = "ClassAI",
                    modifier = Modifier
                        .width(150.dp)
                        .height(42.dp),
                    contentScale = ContentScale.Fit
                )

        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Sign in to continue",
            fontSize = 18.sp,
            color = AppColors.TextSecondary
        )

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        Button(
            onClick = {
                navController.navigate(
                    "${Routes.LOGIN}?type=admin"
                )
            },
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
                text = "Sign in as Admin",
                fontSize = 16.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                navController.navigate(
                    "${Routes.LOGIN}?type=employee"
                )
            },
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
                text = "Sign in as Employee",
                fontSize = 16.sp
            )
        }
    }
}