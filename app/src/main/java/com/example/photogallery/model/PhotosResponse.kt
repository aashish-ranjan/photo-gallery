package com.example.photogallery.model

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotosResponse(@Json(name = "photos") val photosData: GalleryItems, val stat: String)

@JsonClass(generateAdapter = true)
data class GalleryItems(@Json(name = "photo") val photoList: List<Photo>)

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String,
    @Json(name = "url_s") val imageUrl: String = "",
    val title: String,
    val owner: String
) {
    val photoPageUrl: Uri
        get() = Uri.parse("https://flickr.com/photos")
        .buildUpon()
        .appendPath(owner)
        .appendPath(id)
        .build()
}