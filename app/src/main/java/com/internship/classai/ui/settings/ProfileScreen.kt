package com.internship.classai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.theme.AppColors

@Composable
fun ProfileScreen(
    navController: NavHostController
) {

    Scaffold(

        topBar = {

            AppToolbar(
                onMenuClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )

        },



    ) { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(innerPadding)
                .padding(20.dp)

        ) {

            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "View your account information",
                fontSize = 14.sp,
                color = AppColors.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            //==================================================
            // PROFILE HEADER
            //==================================================

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp),

                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                )

            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Box(

                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AppColors.Background),

                        contentAlignment = Alignment.Center

                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = AppColors.PrimaryEnd
                        )

                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Shopnojo",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Administrator",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            //==================================================
            // ACCOUNT INFORMATION
            //==================================================

            Text(
                text = "Account Information",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(10.dp)
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

                    verticalArrangement = Arrangement.spacedBy(18.dp)

                ) {

                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = "admin@classai.com"
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone",
                        value = "+91 XXXXX XXXXX"
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Role",
                        value = "Administrator"
                    )

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Account Status",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Surface
                )

            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Box(

                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AppColors.PrimaryEnd)

                    )

                    Text(
                        text = "Active",
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )

                }

            }

        }

    }

}

@Composable
private fun ProfileInfoRow(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    label: String,

    value: String

) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically

    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = AppColors.PrimaryEnd
        )

        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f)
        ) {

            Text(
                text = label,
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )

        }

    }

}