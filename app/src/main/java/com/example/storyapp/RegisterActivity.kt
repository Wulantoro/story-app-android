package com.example.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp.api.Result
import com.example.storyapp.api.Result.Loading
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.viewmodel.RegistrasiViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registrasiViewModel: RegistrasiViewModel by viewModels {
        RegistrasiViewModel.RegistrasiViewModelFactory.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            registrasi()
        }

    }

    private fun registrasi() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text
            val email = binding.emailEditText.text
            val password = binding.passwordEditText.text
//            if (!name.isNullOrEmpty() && !email.isNullOrEmpty()) {
                val result = registrasiViewModel.registrasi(
                    name.toString(),
                    email.toString(),
                    password.toString()
                )

                result.observe(this) {
                    when (it) {
                        is Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            val error = it.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                this,
                                getString(R.string.registrasi_berhasil),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        }
    }
}
               