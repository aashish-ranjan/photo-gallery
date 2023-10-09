package com.example.photogallery

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.model.Photo
import retrofit2.HttpException
import java.io.IOException

class PhotoPagingSource(
    private val flickrApi: FlickrApi,
    private val query: String
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val currentPageNumber = params.key ?: PHOTOS_STARTING_PAGE_INDEX
        val response =
            if (query.isEmpty()) flickrApi.getPaginatedPhotos(currentPageNumber, params.loadSize)
            else flickrApi.getPaginatedPhotosBySearchQuery(
                query,
                currentPageNumber,
                params.loadSize
            )
        return try {
            val photosList = response.photosData.photoList

            LoadResult.Page(
                data = photosList,
                prevKey = if (currentPageNumber == PHOTOS_STARTING_PAGE_INDEX) null else currentPageNumber - 1,
                nextKey = if (photosList.isEmpty()) null else currentPageNumber + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)

        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val PHOTOS_STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}