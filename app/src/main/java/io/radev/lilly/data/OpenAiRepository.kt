package io.radev.lilly.data

import com.example.featherlyspy.lilly.ErrorResponse
import com.example.featherlyspy.lilly.OpenAIService
import com.haroldadmin.cnradapter.NetworkResponse
import io.radev.lilly.ChatCompletionRequest
import io.radev.lilly.ChatCompletionResponse
import io.radev.lilly.Content
import io.radev.lilly.ImageUrl
import io.radev.lilly.Message
import javax.inject.Inject

class OpenAiRepository @Inject constructor(
    private val openAIService: OpenAIService
) {

    suspend fun analyzeImageWithUrl(imageUrl: String): NetworkResponse<ChatCompletionResponse, ErrorResponse> {
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

    //TODO /Users/radoslawstasiak/dev/radev/StudioProjects/lilly-android/app/src/main/java/io/radev/lilly/UploadViewModel.kt
}
