package com.example.photogallery.api

import com.example.photogallery.model.PhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("services/rest?method=flickr.interestingness.getList")
    suspend fun getPaginatedPhotos(
        @Query("page") pageNumber: Int,
        @Query("per_page") numPhotosPerPage: Int
    ): PhotosResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun getPaginatedPhotosBySearchQuery(
        @Query("text") searchQuery: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") numPhotosPerPage: Int
    ): PhotosResponse

    @GET("services/rest?method=flickr.photos.search")
    suspend fun getPhotosBySearchQuery(
        @Query("text") searchQuery: String,
    ): PhotosResponse
}