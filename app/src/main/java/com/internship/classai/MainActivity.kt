package com.internship.classai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.internship.classai.navigation.AppNavigation
import com.internship.classai.ui.theme.InternshipProjectTheme

class MainActivity : ComponentActivity() {

    private val bluetoothPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            bluetoothPermissionLauncher.launch(

                arrayOf(

                    Manifest.permission.BLUETOOTH_CONNECT,

                    Manifest.permission.BLUETOOTH_SCAN

                )

            )

        }

        setContent {
            InternshipProjectTheme {
                AppNavigation()
            }
        }
    }
}