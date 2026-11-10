package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.PagingRepository
import com.example.storyapp.response.ListStoryItem

class PagingViewModel( private val pagingRepository: PagingRepository
                      ) : ViewModel() {


    fun allStory(token: String): LiveData<PagingData<ListStoryItem>> {
       return pagingRepository.getStory(token).cachedIn(viewModelScope)}

    class PagingViewModelFactory private constructor(
        private val pagingRepository: PagingRepository

    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
                return PagingViewModel(pagingRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: PagingViewModelFactory? = null

            fun getInstance(
                loginPreferences: LoginPreferences
            ): PagingViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: PagingViewModelFactory(
                        Injection.provideStoryPagingRepository()
                    )
                }.also { instance = it }
        }
    }
}