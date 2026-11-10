package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.api.ApiService
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.response.ListStoryItem

class PagingRepository(private val apiService: ApiService) {
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        Log.e("PagingRepository", "token = " + token)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, apiService)
            }
        ).liveData
    }

    companion object {

        @Volatile
        private var instance: PagingRepository? = null

        fun getInstance(
            apiService: ApiService

        ): PagingRepository =
            instance ?: synchronized(this) {
                instance ?: PagingRepository(apiService
                )
            }.also { instance = it }
    }
}