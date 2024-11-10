package io.radev.lilly.domain

import android.util.Log
import com.haroldadmin.cnradapter.NetworkResponse
import io.radev.lilly.ui.upload.UploadState
import io.radev.lilly.data.OpenAiRepository
import javax.inject.Inject

class AnalyzeImageUrlWithGptUseCase @Inject constructor(
    private val openAiRepository: OpenAiRepository
) {

    suspend operator fun invoke(imageUrl: String): UploadState =
        when (val response = openAiRepository.analyzeUrlImage(
            imageUrl = imageUrl
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
}
