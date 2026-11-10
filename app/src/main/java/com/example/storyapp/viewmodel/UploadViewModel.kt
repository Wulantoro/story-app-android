package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.StoryRepository
import java.io.File

class UploadViewModel(
    private val storyRepository: StoryRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel()  {

    fun uploadStory(token: String, imageFile: File, description: String) =
        storyRepository.postStory(token, imageFile, description)

    fun checkToken(): LiveData<String> {
        return loginPreferences.getToken().asLiveData()
    }

    fun getStory(token: String)  = storyRepository.getStori(token)

    class UploadViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val loginPreferences: LoginPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
                return UploadViewModel(storyRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: UploadViewModelFactory? = null

            fun getInstance(
                loginPreferences: LoginPreferences
            ): UploadViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: UploadViewModelFactory(
                        Injection.provideStoryRepository(),
                        loginPreferences
                    )
                }.also { instance = it }
        }
    }
}