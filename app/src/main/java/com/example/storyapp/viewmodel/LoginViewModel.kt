package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository,
                     private val loginPreferences: LoginPreferences
) : ViewModel() {

    fun login(email: String, password: String) = userRepository.login(email, password)
    fun setFirstTime(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.setFirstTime(firstTime)
        }
    }

    fun checkFirstTime(): LiveData<Boolean> {
        return loginPreferences.firstTime().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.saveToken(token)
        }
    }


    class LoginViewModelFactory private constructor(
        private val userRepository: UserRepository,
        private val loginPreferences: LoginPreferences
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(userRepository, loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: LoginViewModelFactory? = null
            fun getInstance(
                loginPreferences: LoginPreferences
            ): LoginViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: LoginViewModelFactory(
                        Injection.provideUserRepository(),
                        loginPreferences
                    )
                }
        }
    }
}