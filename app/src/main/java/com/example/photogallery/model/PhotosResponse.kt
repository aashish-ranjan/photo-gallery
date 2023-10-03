package com.example.photogallery.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PhotosResponse(val photos: List<Photo>, val stat: String)