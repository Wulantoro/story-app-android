package com.example.storyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.api.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(
            LoginPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { login() }

        binding.btnRegistrasi.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        firstCheck()
    }

    private fun firstCheck() {
        loginViewModel.checkFirstTime().observe(this) {
            if (it) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun login() {

        if (!binding.edLoginEmail.text.isNullOrEmpty() && !binding.edLoginEmail.text.isNullOrEmpty()) {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            val result = loginViewModel.login(email, password)

            result.observe(this@LoginActivity) {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val error = it.error
                        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val data = it.data
                        loginViewModel.saveToken(data.loginResult.token)
                        Log.d("LoginActivity", "Token: ${data.loginResult.token}")
                        val intent = Intent(this, AddStoryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        } else {
            binding.edLoginEmail.setError("Email tidak boleh kosong", null)
        }

    }


}