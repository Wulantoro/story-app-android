package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyapp.api.ApiService
import com.example.storyapp.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.storyapp.api.Result
import com.example.storyapp.response.AddStoryResponse
import com.example.storyapp.response.ListStoryItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository constructor(
    private val apiService: ApiService
) {

    private val getStory = MediatorLiveData<List<ListStoryItem>?>()
    private val uploadStoryResult = MediatorLiveData<Result<AddStoryResponse>>()

    fun getStori(token: String): MediatorLiveData<List<ListStoryItem>?> {
        val client = apiService.getStories(token)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                   getStory.postValue(stories)
                } else {
                    Log.e(TAG, "Failed: Get stories response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
        return getStory
    }

    fun postStory(token: String, imageFile: File, description: String): LiveData<Result<AddStoryResponse>> {
        uploadStoryResult.postValue(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)

        val client = apiService.postStory(token, imageMultiPart, descriptionRequestBody)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseInfo = response.body()
                    if (responseInfo != null) {
                        uploadStoryResult.postValue(Result.Success(responseInfo))
                    } else {
                        uploadStoryResult.postValue(Result.Error(ERROR_MESSAGE))
                        Log.e(TAG, "Failed: story post info is null")
                    }
                } else {
                    uploadStoryResult.postValue(Result.Error(ERROR_MESSAGE))
                    Log.e(TAG, "Failed: story post response unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                uploadStoryResult.postValue(Result.Error(ERROR_MESSAGE))
                Log.e(TAG, "Failed: story post response failure - ${t.message.toString()}")
            }
        })

        return uploadStoryResult
    }


    companion object {
        private val TAG = StoryRepository::class.java.simpleName
        private const val ERROR_MESSAGE = "Gagal upload story, mohon coba lagi."

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService

        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService
                    )
            }.also { instance = it }
    }
}



