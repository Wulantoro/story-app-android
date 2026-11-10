package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel (
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel() {

    fun checkIfTokenAvailable(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.deleteToken()
        }
    }

    class MainViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val loginPreferences: LoginPreferences
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(storyRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null

            fun getInstance(

                loginPreferences: LoginPreferences
            ): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideStoryRepository(),
                    loginPreferences
                )
            }.also { instance = it }
        }
    }
}