package com.internship.classai.ui.admin

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.internship.classai.data.model.Admin
import com.internship.classai.data.model.AdminStatusUpdate
import com.internship.classai.data.remote.RetrofitClient
import com.internship.classai.ui.components.AppToolbar
import com.internship.classai.ui.components.ScreenHeader
import com.internship.classai.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun AdminDirectoryScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var admins by remember {
        mutableStateOf<List<Admin>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    /*
     * Load administrators directly from FastAPI.
     */
    LaunchedEffect(Unit) {
        try {
            admins = RetrofitClient.apiService.getAdmins()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to load administrators: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {

        AppToolbar(
            onMenuClick = {
                navController.popBackStack()
            }
        )

        ScreenHeader(
            title = "Administrators",
            subtitle = "Manage administrator accounts"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (isLoading) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Loading administrators...",
                    color = AppColors.TextSecondary
                )
            }

        } else if (admins.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No administrators found",
                    color = AppColors.TextSecondary
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
            ) {
                items(
                    items = admins,
                    key = { admin ->
                        admin.id
                    }
                ) { admin ->

                    AdminAccountCard(
                        admin = admin,
                        onStatusChanged = { newStatus ->

                            /*
                             * Extra client-side protection.
                             * Backend also protects testadmin.
                             */
                            if (admin.userId == "testadmin") {
                                Toast.makeText(
                                    context,
                                    "SuperAdmin cannot be modified",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@AdminAccountCard
                            }

                            coroutineScope.launch {

                                try {

                                    val response =
                                        RetrofitClient.apiService.updateAdminStatus(
                                            adminId = admin.id,
                                            request = AdminStatusUpdate(
                                                isActive = newStatus
                                            )
                                        )

                                    val success =
                                        response["success"] as? Boolean ?: false

                                    val message =
                                        response["message"]?.toString()
                                            ?: "Unknown server response"

                                    if (success) {

                                        admins = admins.map { currentAdmin ->

                                            if (currentAdmin.id == admin.id) {
                                                currentAdmin.copy(
                                                    isActive = newStatus
                                                )
                                            } else {
                                                currentAdmin
                                            }
                                        }

                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } catch (e: Exception) {

                                    Toast.makeText(
                                        context,
                                        "Failed to update admin status: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminAccountCard(
    admin: Admin,
    onStatusChanged: (Int) -> Unit
) {
    Card(
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
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.AdminPanelSettings,
                contentDescription = "Administrator",
                tint = AppColors.PrimaryEnd
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = admin.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "User ID: ${admin.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Mobile: ${admin.mobile}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = if (admin.isActive == 1) {
                        "Active"
                    } else {
                        "Inactive"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Switch(
                checked = admin.isActive == 1,
                onCheckedChange = { checked ->

                    onStatusChanged(
                        if (checked) {
                            1
                        } else {
                            0
                        }
                    )
                },
                enabled = admin.userId != "testadmin"
            )
        }
    }
}