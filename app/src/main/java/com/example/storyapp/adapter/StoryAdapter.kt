package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.DetailActivity
import com.example.storyapp.databinding.ListItemBinding
import com.example.storyapp.response.ListStoryItem

class StoryAdapter :
    PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.tvCaption.text = data.description
            binding.tvUserName.text = data.name

            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.imgPost)

            binding.cardStory.setOnClickListener {
                val intent =Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.NAME, data.name)
                intent.putExtra(DetailActivity.DESCRIPTION, data.description)
                intent.putExtra(DetailActivity.IMAGE_URL, data.photoUrl)
                itemView.context.startActivity(intent)
            }
        }
    }
    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}