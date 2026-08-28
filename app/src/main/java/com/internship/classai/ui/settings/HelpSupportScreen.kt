package com.internship.classai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.theme.AppColors

@Composable
fun HelpSupportScreen(
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
                .padding(20.dp)

        ) {

            Text(
                text = "Help & Support",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "We're here to help",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            SupportItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help Center",
                subtitle = "Find answers to common questions"
            )

            SupportItem(
                icon = Icons.Outlined.Email,
                title = "Email Support",
                subtitle = "Get in touch with our support team"
            )

            SupportItem(
                icon = Icons.Outlined.Phone,
                title = "Contact Support",
                subtitle = "Speak with our support team"
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Need more help?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )

                    Text(
                        text = "If you are experiencing an issue with ClassAI, contact your administrator or support team.",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )

                }

            }

        }

    }

}

@Composable
private fun SupportItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.PrimaryEnd
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = AppColors.TextSecondary
            )

        }

    }

}