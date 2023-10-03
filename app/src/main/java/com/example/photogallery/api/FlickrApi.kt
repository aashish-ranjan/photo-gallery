package com.example.photogallery.api

import com.example.photogallery.PhotosRepository.Companion.API_KEY
import com.example.photogallery.model.PhotosResponse
import retrofit2.http.GET

interface FlickrApi {
    @GET("rest?method=flickr.people.getPublicPhotos&api_key=$API_KEY&user_id=61495424%40N00&extras=url_s&format=json&nojsoncallback=1")
    suspend fun getPhotos(): PhotosResponse
}