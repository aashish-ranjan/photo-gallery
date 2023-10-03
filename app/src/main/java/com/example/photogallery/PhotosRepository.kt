package com.example.photogallery

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.model.Photo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.Exception
import java.lang.IllegalStateException

class PhotosRepository private constructor() {
    companion object {
        const val API_KEY = "1d2c19d89b02d3489dfc6beff4d123c1"
        const val FLICKR_BASE_URL = "https://api.flickr.com/services/"

        private var INSTANCE: PhotosRepository? = null
        fun getInstance(): PhotosRepository {
            return INSTANCE ?: throw IllegalStateException("PhotosRepository is null")
        }

        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE = PhotosRepository()
            }
        }
    }

    private val flickrApi: FlickrApi

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(FLICKR_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    suspend fun getPhotos(): List<Photo> {
        return try {
            val response = flickrApi.getPhotos()
            if (response.stat == "ok") {
                response.photos
            } else {
                throw Exception("Something went wrong!")
            }
        } catch (e: Exception) {
            listOf()
        }
    }
}