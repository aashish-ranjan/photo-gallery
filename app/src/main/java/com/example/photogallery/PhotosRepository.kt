package com.example.photogallery

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.model.Photo
import java.lang.Exception
import java.lang.IllegalStateException

class PhotosRepository private constructor(private val flickrApi: FlickrApi) {
    companion object {
        const val API_KEY = "1d2c19d89b02d3489dfc6beff4d123c1"
        const val FLICKR_BASE_URL = "https://api.flickr.com/services/"

        private var INSTANCE: PhotosRepository? = null
        fun getInstance(): PhotosRepository {
            return INSTANCE ?: throw IllegalStateException("PhotosRepository is null")
        }

        fun initialize(flickrApi: FlickrApi) {
            if (INSTANCE == null) {
                INSTANCE = PhotosRepository(flickrApi)
            }
        }
    }

    suspend fun getPhotos(): List<Photo> {
        return try {
            val response = flickrApi.getPhotos()
            if (response.stat == "ok") {
                response.photosData.photoList
            } else {
                throw Exception("Something went wrong!")
            }
        } catch (e: Exception) {
            listOf()
        }
    }
}