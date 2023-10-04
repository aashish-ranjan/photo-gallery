package com.example.photogallery.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotosResponse(@Json(name = "photos") val photosData: GalleryItems, val stat: String)

@JsonClass(generateAdapter = true)
data class GalleryItems(@Json(name = "photo") val photoList: List<Photo>)

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String,
    @Json(name = "url_s") val imageUrl: String,
    val title: String
)