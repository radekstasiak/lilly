package io.radev.lilly.ui.upload

import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UploadImageScreen(viewModel: UploadViewModel = viewModel(), imageUri: Uri?) {
    val context = LocalContext.current
    var showLoading by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf("") }

    // Collect the upload state from the ViewModel using StateFlow
    LaunchedEffect(viewModel.uploadState) {
        viewModel.uploadState.collectLatest { state ->
            when (state) {
                is UploadState.Success -> {
                    showLoading = false
                    showMessage = "Upload successful!"
                }

                is UploadState.Error -> {
                    showLoading = false
                    showMessage = state.message
                }

                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the captured image
        if (imageUri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Captured Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upload button
        Button(
            onClick = {
                imageUri?.let {
                    showLoading = true
                    viewModel.uploadPhotoToFirebase(it)
                }
            },
            enabled = imageUri != null
        ) {
            Text(text = "Analyze via firebase")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                imageUri?.let {
                    showLoading = true
                    viewModel.analyzeImageBase64WithGPT(it)
                }
            },
            enabled = imageUri != null
        ) {
            Text(text = "Upload Image to GPT directly")
        }

        if (showLoading) {
            CircularProgressIndicator()
        }

        if (showMessage.isNotEmpty()) {
            Text(text = showMessage)
        }
    }
}
