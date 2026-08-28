package com.internship.classai.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.internship.classai.R
import com.internship.classai.ui.theme.AppColors

@Composable
fun AppToolbar(
    onMenuClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        AppColors.PrimaryStart,
                        AppColors.PrimaryEnd
                    )
                )
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Menu",
                tint = AppColors.White,
                modifier = Modifier.clickable {
                    onMenuClick()
                }
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(R.drawable.classai_logo),
                    contentDescription = null,
                    modifier = Modifier.height(28.dp),
                    contentScale = ContentScale.Fit
                )

            }

            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = AppColors.White
            )

        }
    }
}