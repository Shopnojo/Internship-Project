package com.internship.classai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.HelpOutline
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
fun SettingsScreen(
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(20.dp)

        ) {

            //==================================================
            // HEADER
            //==================================================

            Text(

                text = "Settings",

                fontSize = 28.sp,

                fontWeight = FontWeight.Bold,

                color = AppColors.TextPrimary

            )

            Spacer(

                modifier = Modifier.height(4.dp)

            )

            Text(

                text = "Manage your account",

                fontSize = 14.sp,

                color = AppColors.TextSecondary

            )

            Spacer(

                modifier = Modifier.height(28.dp)

            )

            //==================================================
            // ACCOUNT
            //==================================================

            SectionTitle(

                title = "Account"

            )

            Spacer(

                modifier = Modifier.height(8.dp)

            )

            SettingsItem(
                icon = Icons.Outlined.Person,
                title = "Profile",
                subtitle = "View and manage your profile",
                onClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )

            SettingsItem(
                icon = Icons.Outlined.Lock,
                title = "Change Password",
                subtitle = "Update your account password",
                onClick = {
                    navController.navigate(Routes.CHANGE_PASSWORD)
                }
            )

            Spacer(

                modifier = Modifier.height(24.dp)

            )

            //==================================================
            // SUPPORT
            //==================================================

            SectionTitle(

                title = "Support"

            )

            Spacer(

                modifier = Modifier.height(8.dp)

            )

            SettingsItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help & Support",
                subtitle = "Get help with ClassAI",
                onClick = {
                    navController.navigate(Routes.HELP_SUPPORT)
                }
            )

            SettingsItem(
                icon = Icons.Outlined.Info,
                title = "About ClassAI",
                subtitle = "Learn more about ClassAI",
                onClick = {
                    navController.navigate(Routes.ABOUT_CLASS_AI)
                }
            )

            Spacer(

                modifier = Modifier.height(28.dp)

            )

            //==================================================
            // LOGOUT
            //==================================================

            SettingsItem(

                icon = Icons.Outlined.Logout,

                title = "Logout",

                subtitle = "Sign out of your account",

                onClick = {
                    // TODO: Logout
                }

            )

        }

    }

}

//==================================================
// SECTION TITLE
//==================================================

@Composable
private fun SectionTitle(

    title: String

) {

    Text(

        text = title,

        fontSize = 15.sp,

        fontWeight = FontWeight.SemiBold,

        color = AppColors.TextSecondary

    )

}

//==================================================
// SETTINGS ITEM
//==================================================

@Composable
private fun SettingsItem(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    title: String,

    subtitle: String,

    onClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                onClick()

            }
            .padding(

                vertical = 14.dp

            ),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.Start

    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint = AppColors.PrimaryEnd

        )

        Column(

            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)

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