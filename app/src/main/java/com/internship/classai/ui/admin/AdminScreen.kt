package com.internship.classai.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.navigation.Routes
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.theme.AppColors

@Composable
fun AdminScreen(
    navController: NavHostController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {

        AppToolbar(
            onMenuClick = {
                navController.navigate(Routes.SETTINGS)
            }
        )

        ScreenHeader(
            title = "Admin Dashboard",
            subtitle = "Manage ClassAI users and accounts"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AdminActionCard(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = "Employees",
                        tint = AppColors.PrimaryEnd
                    )
                },
                title = "Employees",
                subtitle = "View and manage employee accounts",
                onClick = {
                    navController.navigate(
                        Routes.EMPLOYEE_DIRECTORY
                    )
                }
            )

            AdminActionCard(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Administrators",
                        tint = AppColors.PrimaryEnd
                    )
                },
                title = "Administrators",
                subtitle = "View and manage administrator accounts",
                onClick = {
                    navController.navigate(
                        Routes.ADMIN_DIRECTORY
                    )
                }
            )

            AdminActionCard(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = "Add user",
                        tint = AppColors.PrimaryEnd
                    )
                },
                title = "Add New User",
                subtitle = "Create a new employee or administrator",
                onClick = {
                    navController.navigate(
                        Routes.ADD_NEW_USER
                    )
                }
            )
        }

        Button(
            onClick = {
                navController.navigate(
                    Routes.STUDENTS
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryEnd,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Payments"
            )
        }
    }
}

@Composable
private fun AdminActionCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            icon()

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}