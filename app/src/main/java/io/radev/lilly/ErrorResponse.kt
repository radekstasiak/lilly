package com.example.featherlyspy.lilly

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: Error
) {
    @JsonClass(generateAdapter = true)
    data class Error(
        val message: String?,
        val type: String?,
        val param: String?,
        val code: String?
    )
}
