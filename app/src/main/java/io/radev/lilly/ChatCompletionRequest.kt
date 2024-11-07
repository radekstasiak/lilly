package io.radev.lilly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<Message>,
    @Json(name = "max_tokens") val maxTokens: Int
)

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: List<Content>
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "type") val type: String,
    @Json(name = "text") val text: String? = null,
    @Json(name = "image_url") val imageUrl: ImageUrl? = null
)

@JsonClass(generateAdapter = true)
data class ImageUrl(
    @Json(name = "url") val url: String
)
