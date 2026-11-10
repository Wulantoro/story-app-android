package com.example.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetail()
    }

private fun getDetail(){
    val name = intent.getStringExtra(NAME)
    val description = intent.getStringExtra(DESCRIPTION)
    val imgUrl = intent.getStringExtra(IMAGE_URL)

    binding.tvName.text = name
    binding.tvDesc.text = description

    Glide.with(this)
        .load(imgUrl)
        .into(binding.ivAvatar)
}

    companion object {
        const val NAME = "name_extra"
        const val DESCRIPTION = "desc_extra"
        const val IMAGE_URL = "img_extra"
    }
}