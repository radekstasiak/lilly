package com.example.featherlyspy.lilly

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyzeImageRequest(
    val model: String = "gpt-4o",  // Specify GPT-4 Vision model
    val messages: List<Message>,         // Messages, including system, user, or image prompt
//    val image: String? = null            // Optionally add image URL or content here
) {
    @JsonClass(generateAdapter = true)
    data class Message(
        val role: String,                    // "system", "user", "assistant"
        val content: String? = null          // Text message from user or system
    )
}

