package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.api.ApiService
import com.example.storyapp.response.ListStoryItem

class StoryPagingSource(private val token: String, private val apiService: ApiService): PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        Log.e("paging", "token = " + token)
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesWithPaging(token, page, params.loadSize)
            val data = responseData.listStory
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            Log.e("paging", "exception = " + exception.message)
            return LoadResult.Error(exception)
        }
    }
}