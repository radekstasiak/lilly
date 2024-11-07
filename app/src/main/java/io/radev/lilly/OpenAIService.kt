package com.example.featherlyspy.lilly

import com.haroldadmin.cnradapter.NetworkResponse
import io.radev.lilly.ChatCompletionRequest
import io.radev.lilly.ChatCompletionResponse
import io.radev.lilly.ImageAnalyzeRequest
import io.radev.lilly.ImageAnalyzeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenAIService {

    // Upload image file
    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("purpose") purpose: RequestBody
    ): NetworkResponse<UploadResponse, ErrorResponse>

    // Analyze the uploaded image
    @POST("chat/completions")
    suspend fun analyzeImage(
        @Body request: AnalyzeImageRequest
    ): NetworkResponse<AnalysisResponse, ErrorResponse>

    @POST("chat/completions")
    suspend fun analyzeImage(
        @Body request: ChatCompletionRequest
    ): NetworkResponse<ChatCompletionResponse, ErrorResponse>

    @POST("images/analyze")  // Replace with actual endpoint path
    suspend fun analyzeImage(@Body request: ImageAnalyzeRequest): NetworkResponse<ImageAnalyzeResponse, ErrorResponse>
}
