package com.example.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.MapRepository
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val mapRepository: MapRepository,
                   private val loginPreferences: LoginPreferences) : ViewModel() {

    private val _listLocationStory = MutableLiveData<List<ListStoryItem>>()

    val listLocationStory: LiveData<List<ListStoryItem>> = _listLocationStory
    private val client = ApiConfig.getApiService()

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

     fun getStoryMap(token: String)  = mapRepository.getStoriMap(token)

    class MapViewModelFactory private constructor(

        private val mapRepository: MapRepository,
        private val loginPreferences: LoginPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                return MapViewModel(mapRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MapViewModelFactory? = null

            fun getInstance(
                loginPreferences: LoginPreferences
            ): MapViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: MapViewModelFactory(
                        Injection.provideStoryMapRepository(),
                        loginPreferences

                    )
                }.also { instance = it }
        }

    }
}