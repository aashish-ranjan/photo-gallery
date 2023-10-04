package com.example.photogallery

import android.app.Application
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.UrlInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PhotoGalleryApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(UrlInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(PhotosRepository.FLICKR_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        val flickrApi = retrofit.create(FlickrApi::class.java)
        PhotosRepository.initialize(flickrApi)
    }
}