package com.example.featherlyspy.lilly

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalysisResponse(
    val result: String  // Response structure from OpenAI
)
