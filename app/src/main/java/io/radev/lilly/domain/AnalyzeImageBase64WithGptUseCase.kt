package io.radev.lilly.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import io.radev.lilly.ui.upload.UploadState
import io.radev.lilly.data.OpenAiRepository
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AnalyzeImageBase64WithGptUseCase @Inject constructor(
    private val openAiRepository: OpenAiRepository,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(imageUrl: Uri): UploadState =
        when (val response = openAiRepository.analyzeBase64Image(
            imageUrl = encodeImageToBase64(imageUrl).orEmpty()
        )) {
            is NetworkResponse.Success -> {
                // Handle successful analysis result
                Log.d("gpt upload", response.body.toString())
                UploadState.Success(response.body.toString())
            }

            is NetworkResponse.ServerError -> {
                UploadState.Error("Server error: ${response.body?.error}")
            }

            is NetworkResponse.NetworkError -> {
                UploadState.Error("Network error: ${response.error.message}")
            }

            is NetworkResponse.UnknownError -> {
                UploadState.Error("Unknown error: ${response.error?.message}")
            }
        }

    private fun encodeImageToBase64(imageUri: Uri): String? {
        return try {
            // Open an InputStream from the provided image URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            return encodeBitmapToBase64(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun encodeBitmapToBase64(bitmap: Bitmap, quality: Int = 50): String {
        val outputStream = ByteArrayOutputStream()
        // Compress the bitmap to JPEG with specified quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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
