package com.example.featherlyspy.lilly

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraLaunchScreen(onLaunchCamera: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Permissions granted!")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLaunchCamera) {
            Text(text = "Launch Camera")
        }
    }
}
