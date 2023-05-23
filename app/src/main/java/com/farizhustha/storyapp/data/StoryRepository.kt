package com.farizhustha.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.farizhustha.storyapp.database.StoryDatabase
import com.farizhustha.storyapp.service.remote.ApiService
import com.farizhustha.storyapp.service.remote.response.Story

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
}