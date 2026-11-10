package com.example.storyapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyapp.api.ApiService
import com.example.storyapp.response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.storyapp.api.Result
import com.example.storyapp.response.RegisterResponse


class UserRepository private constructor(
    private val apiService: ApiService
)  {

    private val loginResult = MediatorLiveData<Result<LoginResponse>>()
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading
        val client = apiService.login(
            email,
            password
        )

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginInfo = response.body()
                    if (loginInfo != null) {
                        loginResult.value = Result.Success(loginInfo)
                    } else {
                        loginResult.value = Result.Error(LOGIN_ERROR)
                        Log.e(TAG, "Failed: Login Info is null")
                    }
                } else {
                    loginResult.value = Result.Error(LOGIN_ERROR)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(LOGIN_ERROR)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }
        })

        return loginResult
    }

    fun registrasi(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        val client = apiService.register(
            name,
            email,
            password
        )

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerInfo = response.body()
                    if (registerInfo != null) {
                        registerResult.value = Result.Success(registerInfo)
                    } else {
                        registerResult.value = Result.Error(REGISTRASI_ERROR)
                        Log.e(TAG, "Failed: Register Info is null")
                    }
                } else {
                    registerResult.value = Result.Error(REGISTRASI_ERROR)
                    Log.e(TAG, "Failed: Response Unsuccessful - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(REGISTRASI_ERROR)
                Log.e(TAG, "Failed: Response Failure - ${t.message.toString()}")
            }

        })

        return registerResult
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
        private const val LOGIN_ERROR = "Login gagal, silahkan coba lagi."
        private const val REGISTRASI_ERROR = "Register gagal, silahkan coba lagi."

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}