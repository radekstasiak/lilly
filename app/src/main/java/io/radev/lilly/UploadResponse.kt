package com.example.featherlyspy.lilly

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "id") val id: String,
    @Json(name = "object") val obj: String,
    @Json(name = "created_at") val createdAt: Int,
    @Json(name = "filename") val filename: String,
    @Json(name = "purpose") val purpose: String,
    @Json(name = "bytes") val bytes: Int
)
