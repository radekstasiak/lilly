package io.radev.lilly.data.model.chatcompletion

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    @Json(name = "choices") val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "message") val message: MessageContent
)

@JsonClass(generateAdapter = true)
data class MessageContent(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)
