package com.example.storyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.databinding.ActivityWelcomeBinding
import com.example.storyapp.datastore.LoginPreferences
import com.example.storyapp.viewmodel.LoginViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val welcomeViewModel: LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(LoginPreferences.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            welcomeViewModel.setFirstTime(false)
            startActivity(intent)
        }
    }
}