package com.farizhustha.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.farizhustha.storyapp.database.RemoteKeys
import com.farizhustha.storyapp.database.StoryDatabase
import com.farizhustha.storyapp.model.AddStory
import com.farizhustha.storyapp.service.remote.ApiService
import com.farizhustha.storyapp.service.remote.response.Story
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    private suspend fun getAllStories() {
        try {
            val page = 1
            val size = 5
            val response = apiService.getStoriesWithPaging(page, size)
            storyDatabase.remoteKeysDao().deleteRemoteKeys()
            storyDatabase.storyDao().deleteAll()
            val listStory = response.listStory
            if (!listStory.isNullOrEmpty()) {
                val keys = listStory.map {
                    RemoteKeys(id = it.id, prevKey = null, nextKey = 2)
                }
                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insertStory(listStory)
            }
        } catch (_: Exception) {
        }
    }

    suspend fun addStory(photo: MultipartBody.Part, description: RequestBody) {
        try {
            apiService.addStory(photo, description)
            getAllStories()

        } catch (_: Exception) {
        }
    }

    suspend fun addStoryWithLocation(item: AddStory) {
        try {
            if (item.lat != null && item.lon != null) {
                apiService.addStoryWithLocation(item.photo, item.description, item.lat, item.lon)
                getAllStories()
            }

        } catch (_: Exception) {
        }
    }
}