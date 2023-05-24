package com.farizhustha.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.farizhustha.storyapp.service.local.room.RemoteKeys
import com.farizhustha.storyapp.service.local.room.StoryDatabase
import com.farizhustha.storyapp.model.AddStory
import com.farizhustha.storyapp.service.remote.ApiService
import com.farizhustha.storyapp.service.remote.response.Story
import org.json.JSONObject
import retrofit2.HttpException

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

    fun getAllStoriesWithLocation(): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.getAllStories(1)
            val listStory = response.listStory
            if (listStory != null) {
                emit(Result.Success(listStory))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                JSONObject(it).getString("message")
            }
            emit(Result.Error(errorMessage ?: "Error"))
        }
    }

    fun getDetailStory(id: String) = storyDatabase.storyDao().getDetailStory(id)

    private suspend fun getStoriesWithPaging() {
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

    fun addStory(item: AddStory): LiveData<Result<String>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.addStory(item.photo, item.description)
                val message = response.message
                getStoriesWithPaging()
                if (message != null) {
                    emit(Result.Success(message))
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    JSONObject(it).getString("message")
                }
                emit(Result.Error(errorMessage ?: "Error"))
            }
        }

    fun addStoryWithLocation(item: AddStory): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            if (item.lat != null && item.lon != null) {
                val response = apiService.addStoryWithLocation(
                    item.photo,
                    item.description,
                    item.lat,
                    item.lon
                )
                val message = response.message
                getStoriesWithPaging()
                if (message != null) {
                    emit(Result.Success(message))
                }
            }

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                JSONObject(it).getString("message")
            }
            emit(Result.Error(errorMessage ?: "Error"))
        }
    }
}