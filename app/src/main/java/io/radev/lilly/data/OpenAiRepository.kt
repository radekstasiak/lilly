package io.radev.lilly.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import io.radev.lilly.data.model.ErrorResponse
import com.haroldadmin.cnradapter.NetworkResponse
import io.radev.lilly.data.model.chatcompletion.ChatCompletionRequest
import io.radev.lilly.data.model.chatcompletion.ChatCompletionResponse
import io.radev.lilly.data.model.chatcompletion.Content
import io.radev.lilly.data.model.chatcompletion.ImageUrl
import io.radev.lilly.data.model.chatcompletion.Message
import io.radev.lilly.network.OpenAIService
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class OpenAiRepository @Inject constructor(
    private val openAIService: OpenAIService
) {

    suspend fun analyzeUrlImage(imageUrl: String): NetworkResponse<ChatCompletionResponse, ErrorResponse> {
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

        return openAIService.analyzeImage(request)
    }

    //TODO https://platform.openai.com/docs/guides/vision/vision
    suspend fun analyzeBase64Image(imageUrl: String): NetworkResponse<ChatCompletionResponse, ErrorResponse> {
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
                                url = "data:image/jpeg;base64,${imageUrl}"
                            )
                        )
                    )
                )
            ),
            maxTokens = 300
        )

        return openAIService.analyzeImage(request)
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
