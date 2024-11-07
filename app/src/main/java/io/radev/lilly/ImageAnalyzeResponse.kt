package io.radev.lilly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ImageAnalyzeResponse(
    @Json(name = "description") val description: String,  // Adjust based on response format
    @Json(name = "tags") val tags: List<String>
)
