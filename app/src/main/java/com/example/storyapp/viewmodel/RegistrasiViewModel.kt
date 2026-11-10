package com.example.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.UserRepository

class RegistrasiViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun registrasi(name: String, email: String, password: String) =
        userRepository.registrasi(name, email, password)

    class RegistrasiViewModelFactory private constructor(private val userRepository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrasiViewModel::class.java)) {
                return RegistrasiViewModel(userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: RegistrasiViewModelFactory? = null

            fun getInstance(): RegistrasiViewModelFactory = instance ?: synchronized(this) {
                instance ?: RegistrasiViewModelFactory(Injection.provideUserRepository())
            }
        }
    }
}