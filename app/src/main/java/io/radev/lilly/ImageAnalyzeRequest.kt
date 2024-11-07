package io.radev.lilly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageAnalyzeRequest(
    @Json(name = "image_url") val imageUrl: String
)

