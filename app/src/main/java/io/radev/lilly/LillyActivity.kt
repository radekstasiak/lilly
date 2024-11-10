package io.radev.lilly

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.FileProvider
import com.example.featherlyspy.lilly.CameraLaunchScreen
import com.example.featherlyspy.lilly.PermissionCheckScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import io.radev.lilly.ui.theme.LillyTheme
import io.radev.lilly.ui.upload.UploadImageScreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LillyActivity : ComponentActivity() {

    private val imageUri: Uri by lazy {
        val photoFile: File = createImageFile()
        FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",  // Use the authority from Manifest
            photoFile
        )
    }
    private lateinit var currentPhotoPath: String

    // Register for camera capture contract
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // Photo successfully captured, imageUri contains the photo file Uri
                setContent {
                    LillyTheme {
                        Surface(color = MaterialTheme.colorScheme.background) {
                            UploadImageScreen(imageUri = imageUri)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
        // Set the content to the permission check screen first
        setContent {
            PermissionCheckScreen(
                onPermissionGranted = { launchCameraScreen() }
            )
        }
    }

    // Launches the camera flow after permissions are granted
    private fun launchCameraScreen() {
        setContent {
            CameraLaunchScreen(
                onLaunchCamera = { takePicture() }
            )
        }
    }

    // Function to take a picture using the camera
    private fun takePicture() {
        // Create a temporary file to save the image

        takePictureLauncher.launch(imageUri)
    }

    // Helper function to create a temporary image file
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(null)  // External files directory for your app
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}
