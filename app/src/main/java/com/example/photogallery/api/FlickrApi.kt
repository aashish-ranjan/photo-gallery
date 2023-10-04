package com.example.photogallery.api

import com.example.photogallery.model.PhotosResponse
import retrofit2.http.GET

interface FlickrApi {
    @GET("services/rest?method=flickr.interestingness.getList")
    suspend fun getPhotos(): PhotosResponse
}