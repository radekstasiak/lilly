package com.example.featherlyspy.lilly

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.radev.lilly.ChatCompletionRequest
import io.radev.lilly.Content
import io.radev.lilly.ImageAnalyzeRequest
import io.radev.lilly.ImageUrl
import io.radev.lilly.Message
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class UploadViewModel : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> get() = _uploadState

    val service by lazy {
        OpenAIClient.getClient().create(OpenAIService::class.java)
    }

    val storage by lazy { Firebase.storage }

    //todo https://platform.openai.com/docs/guides/vision/vision
    fun analyzeImageWithGPT(imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val base64Image = encodeImageToBase64(context, imageUri)

//                val inputStream = context.contentResolver.openInputStream(imageUri)
//                val bitmap = BitmapFactory.decodeStream(inputStream)
//                inputStream?.close()
//                val resizedBitmap =
//                    resizeAndCompressBitmap(bitmap, 512, 512, 75) // Exam

//                if (resizedBitmap != null) {
                // Create the request to send to GPT-4 Vision
                val analyzeRequest = AnalyzeImageRequest(
                    model = "gpt-4o",
                    messages = listOf(
                        AnalyzeImageRequest.Message(
                            role = "user",
                            content = "Please name this tarot card: $imageUrl"
                        ),
//                            AnalyzeImageRequest.Message(role = "user", content = base64Image)
                    )
                )

//                val service = OpenAIClient.getClient().create(OpenAIService::class.java)
//                val response = service.analyzeImage(analyzeRequest)
                val request = ChatCompletionRequest(
                    model = "gpt-4o-mini",
                    messages = listOf(
                        Message(
                            role = "user",
                            content = listOf(
                                Content(type = "text", text = "What tarot cards are in the image?"),
                                Content(
                                    type = "image_url",
                                    imageUrl = ImageUrl(
                                        url = imageUrl
                                    )
                                )
                            )
                        )
                    ),
                    maxTokens = 300
                )

                val response = service.analyzeImage(request)
//                val response = service.analyzeImage(ImageAnalyzeRequest(imageUrl))

                when (response) {
                    is NetworkResponse.Success -> {
                        // Handle successful analysis result
                        Log.d("gpt upload", response.body.toString())
                        _uploadState.value = UploadState.Success
                    }

                    is NetworkResponse.ServerError -> {
                        _uploadState.value =
                            UploadState.Error("Server error: ${response.body?.error}")
                    }

                    is NetworkResponse.NetworkError -> {
                        _uploadState.value =
                            UploadState.Error("Network error: ${response.error.message}")
                    }

                    is NetworkResponse.UnknownError -> {
                        _uploadState.value =
                            UploadState.Error("Unknown error: ${response.error?.message}")
                    }
                }
//            } else {
//            _uploadState.value = UploadState.Error("Failed to encode image.")
//        }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error during image analysis: ${e.message}")
            }
        }
    }

    // Function to upload the image
    fun uploadImage(context: Context, imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get a temporary file from the content URI
                val file = getFileFromUri(context, imageUri)

                if (file != null) {
                    val requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    // Updated the purpose parameter to a valid value
                    val purpose = RequestBody.create(
                        "multipart/form-data".toMediaTypeOrNull(),
                        "vision"
                    ) // or other valid purposes


                    val response = service.uploadFile(body, purpose)

                    when (response) {
                        is NetworkResponse.Success -> {
//                            _uploadState.value = UploadState.Success
                            analyzImage(response.body)
                        }

                        is NetworkResponse.ServerError -> {
                            _uploadState.value =
                                UploadState.Error("Server error: ${response.body?.error}")
                        }

                        is NetworkResponse.NetworkError -> {
                            _uploadState.value =
                                UploadState.Error("Network error: ${response.error.message}")
                        }

                        is NetworkResponse.UnknownError -> {
                            _uploadState.value =
                                UploadState.Error("Unknown error: ${response.error?.message}")
                        }
                    }
                } else {
                    _uploadState.value = UploadState.Error("Failed to get file from URI.")
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Failed to upload image: ${e.message}")
            }
        }
    }

    fun uploadPhotoToFirebase(
        uri: Uri
    ) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imagesRef.putFile(uri)
            .addOnSuccessListener {
                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("upload result", downloadUri.toString())
//                    _uploadState.value = UploadState.Success
                    analyzeImageWithGPT(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Log.d("upload result", it.toString())
                _uploadState.value = UploadState.Error(it.toString())
            }
    }

    suspend fun analyzImage(uploadResponse: UploadResponse) {
        // File uploaded successfully, get the file ID from the response
        val fileId = uploadResponse.id

        // Call the analyzeImage function with the uploaded file ID

        // Create the request for analyzing the image using GPT-4 Vision
        val analyzeRequest = AnalyzeImageRequest(
            model = "gpt-4o",
            messages = listOf(
                AnalyzeImageRequest.Message(role = "user", content = "Analyze the following image"),
                AnalyzeImageRequest.Message(
                    role = "user",
                    content = null
                )  // If you have other text inputs
            ),
//            image = fileId // Assuming you can use the uploaded file ID directly
        )
        val analyzeResponse = service.analyzeImage(analyzeRequest)

        when (analyzeResponse) {
            is NetworkResponse.Success -> {
                // Handle successful analysis
                _uploadState.value = UploadState.Success  // Or handle analysis result as needed
            }

            is NetworkResponse.ServerError -> {
                _uploadState.value =
                    UploadState.Error("Analysis server error: ${analyzeResponse.body?.error}")
            }

            is NetworkResponse.NetworkError -> {
                _uploadState.value =
                    UploadState.Error("Analysis network error: ${analyzeResponse.error.message}")
            }

            is NetworkResponse.UnknownError -> {
                _uploadState.value =
                    UploadState.Error("Unknown error during analysis: ${analyzeResponse.error?.message}")
            }
        }
    }

    // Helper method to convert content Uri to File
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFile = createTempFile(context)

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Create a temporary file
    private fun createTempFile(context: Context): File {
        val tempFile = File(context.cacheDir, "temp_image_upload.jpg")
        tempFile.createNewFile()
        return tempFile
    }

    fun encodeImageToBase64(context: Context, imageUri: Uri): String? {
        return try {
            // Open an InputStream from the provided image URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val resizedBitmap =
                resizeAndCompressBitmap(bitmap, 512, 512, 75) // Example max dimensions
            // Convert the Bitmap to a ByteArrayOutputStream
            val byteArrayOutputStream = ByteArrayOutputStream()
            val imageBytes = byteArrayOutputStream.toByteArray()

            // Encode the byte array to a Base64 string
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resizeAndCompressBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): Bitmap {
        val aspectRatio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (aspectRatio > 1) {
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * aspectRatio).toInt()
        }

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        // Compress the resized bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        return resizedBitmap
    }

}

sealed class UploadState {
    object Idle : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}
