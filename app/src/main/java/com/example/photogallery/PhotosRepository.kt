package com.example.photogallery

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.model.Photo
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import java.lang.IllegalStateException

class PhotosRepository private constructor(private val flickrApi: FlickrApi) {
    companion object {
        const val API_KEY = "1d2c19d89b02d3489dfc6beff4d123c1"
        const val FLICKR_BASE_URL = "https://www.flickr.com/"

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

    suspend fun getPhotos(searchQuery: String): List<Photo> {
        return try {
            val response = flickrApi.getPhotosBySearchQuery(searchQuery)
            if (response.stat == "ok") {
                response.photosData.photoList
            } else {
                listOf()
            }
        } catch (e: Exception) {
            listOf()
        }
    }


    fun getPaginatedPhotos(searchQuery: String): Flow<PagingData<Photo>> {
        return Pager(
            PagingConfig(pageSize = 20)
        ) {
            PhotoPagingSource(flickrApi, searchQuery)
        }.flow
    }
}