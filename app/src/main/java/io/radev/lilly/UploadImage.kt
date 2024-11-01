package com.example.featherlyspy.lilly

import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

suspend fun uploadImage(imageFile: File) {
    val service = OpenAIClient.getClient().create(OpenAIService::class.java)

    val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)
    val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

    val purpose = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "answers")

    when (val response = service.uploadFile(body, purpose)) {
        is NetworkResponse.Success -> {
            val fileId = response.body.id  // Get the file ID from the response
            analyzeImage(fileId)
        }

        is NetworkResponse.ServerError -> {
            // Handle server error (e.g., 5xx response)
            response.body?.let { println("Server Error: ${it.error}") }
        }

        is NetworkResponse.NetworkError -> {
            // Handle network error (e.g., no internet connection)
            println("Network Error: ${response.error}")
        }

        is NetworkResponse.UnknownError -> {
            // Handle unknown errors (e.g., parsing issues)
            println("Unknown Error: ${response.error}")
        }
    }
}

suspend fun analyzeImage(fileId: String) {
    val service = OpenAIClient.getClient().create(OpenAIService::class.java)

    // Create the request for analyzing the image using GPT-4 Vision
    val analyzeRequest = AnalyzeImageRequest(
        model = "gpt-4-vision",
        messages = listOf(
            AnalyzeImageRequest.Message(role = "user", content = "Analyze the following image"),
            AnalyzeImageRequest.Message(
                role = "user",
                content = null
            )  // If you have other text inputs
        ),
//        image = fileId // Assuming you can use the uploaded file ID directly
    )
    // Example model

    when (val response = service.analyzeImage(analyzeRequest)) {
        is NetworkResponse.Success -> {
            // Handle successful analysis
            val result = response.body.result
            println("Analysis Result: $result")
        }

        is NetworkResponse.ServerError -> {
            // Handle server error (e.g., 5xx response)
            response.body?.let { println("Server Error: ${it.error}") }
        }

        is NetworkResponse.NetworkError -> {
            // Handle network error (e.g., no internet connection)
            println("Network Error: ${response.error}")
        }

        is NetworkResponse.UnknownError -> {
            // Handle unknown errors (e.g., parsing issues)
            println("Unknown Error: ${response.error}")
        }
    }
}
