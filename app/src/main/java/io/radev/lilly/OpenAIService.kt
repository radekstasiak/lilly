package com.example.featherlyspy.lilly

import com.haroldadmin.cnradapter.NetworkResponse
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
}
