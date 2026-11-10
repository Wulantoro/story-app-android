package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import com.example.storyapp.api.ApiService
import com.example.storyapp.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import com.example.storyapp.response.ListStoryItem
import retrofit2.Response

class MapRepository constructor(
    private val apiService: ApiService
) {
    private val getStoryLoc = MediatorLiveData<List<ListStoryItem>?>()

     fun getStoriMap(token: String): MediatorLiveData<List<ListStoryItem>?> {
        val client = apiService.getStoriesWithLocation(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                    getStoryLoc.postValue(stories)
                } else {
                    Log.e(TAG, "Failed: Get stories response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG, "Failed: Get stories response unsuccessful - ${t.message.toString()}")
            }
        })
        return getStoryLoc
    }
    companion object {
        private val TAG = MapRepository::class.java.simpleName

        @Volatile
        private var instance: MapRepository? = null

        fun getInstance(
            apiService: ApiService

        ): MapRepository =
            instance ?: synchronized(this) {
                instance ?: MapRepository(apiService
                )
            }.also { instance = it }
    }
}