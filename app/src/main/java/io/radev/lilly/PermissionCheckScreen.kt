package com.example.featherlyspy.lilly

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp

@Composable
fun PermissionCheckScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    // Permissions to request
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // State to manage whether permissions are granted
    var permissionGranted by remember { mutableStateOf(false) }

    // Launcher to request multiple permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        permissionGranted = permissionsMap.values.all { it }
    }

    LaunchedEffect(Unit) {
        // Check if permissions are already granted
        if (permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            // If already granted, proceed to the next step
            onPermissionGranted()
        } else {
            // Otherwise, request the permissions
            requestPermissionLauncher.launch(permissions)
        }
    }

    // UI content
    if (permissionGranted) {
        // Permissions granted, call the next step
        onPermissionGranted()
    } else {
        // Display UI asking user for permissions
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please grant camera and storage permissions to continue.")
        }
    }
}
