package io.radev.lilly.network

import io.radev.lilly.data.model.upload.UploadResponse
import com.haroldadmin.cnradapter.NetworkResponse
import io.radev.lilly.data.model.chatcompletion.ChatCompletionRequest
import io.radev.lilly.data.model.chatcompletion.ChatCompletionResponse
import io.radev.lilly.data.model.ErrorResponse
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


    @POST("chat/completions")
    suspend fun analyzeImage(
        @Body request: ChatCompletionRequest
    ): NetworkResponse<ChatCompletionResponse, ErrorResponse>

}
