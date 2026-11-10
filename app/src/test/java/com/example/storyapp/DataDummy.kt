package com.example.storyapp

import com.example.storyapp.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val stories = ListStoryItem(
                createdAt = "",
                name = "name $i",
                description = "description $i",
                lon = i.toDouble(),
                id = "id $i",
                lat = i.toDouble(),
                photoUrl = "0"
            )
            items.add(stories)
        }
        return items
    }
}