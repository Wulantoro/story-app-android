package com.example.storyapp.di

import com.example.storyapp.api.ApiConfig
import com.example.storyapp.repository.MapRepository
import com.example.storyapp.repository.PagingRepository
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository

object Injection {

    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }

    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }

    fun provideStoryPagingRepository(): PagingRepository {
        val apiService = ApiConfig.getApiService()
        return PagingRepository.getInstance(apiService)
    }

    fun provideStoryMapRepository(): MapRepository {
        val apiService = ApiConfig.getApiService()
        return MapRepository.getInstance(apiService)
    }
}