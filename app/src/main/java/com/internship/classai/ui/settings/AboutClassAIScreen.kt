package com.internship.classai.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.R
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.theme.AppColors

@Composable
fun AboutClassAIScreen(
    navController: NavHostController
) {

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
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Image(

                painter = painterResource(
                    R.drawable.classai_logo
                ),

                contentDescription = "ClassAI Logo",

                modifier = Modifier
                    .size(150.dp)

            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(

                text = "ClassAI Payments App",

                fontSize = 24.sp,

                fontWeight = FontWeight.Bold,

                color = AppColors.TextPrimary,

                textAlign = TextAlign.Center

            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(

                text = "A simple and efficient platform for managing student payments and fee collections.",

                fontSize = 14.sp,

                color = AppColors.TextSecondary,

                textAlign = TextAlign.Center,

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                )

            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {

                    Text(

                        text = "About",

                        fontSize = 16.sp,

                        fontWeight = FontWeight.SemiBold,

                        color = AppColors.TextPrimary

                    )

                    Text(

                        text = "ClassAI helps educational institutions manage students, pending dues, payments and collection records from one place.",

                        fontSize = 14.sp,

                        color = AppColors.TextSecondary

                    )

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(

                text = "Version 1.0.0",

                fontSize = 13.sp,

                color = AppColors.TextSecondary

            )

        }

    }

}