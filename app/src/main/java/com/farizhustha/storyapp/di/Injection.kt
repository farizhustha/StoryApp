package com.farizhustha.storyapp.di

import android.content.Context
import com.farizhustha.storyapp.data.AuthRepository
import com.farizhustha.storyapp.data.StoryRepository
import com.farizhustha.storyapp.service.local.room.StoryDatabase
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.ui.main.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {

        val database = StoryDatabase.getDatabase(context)
        val pref = UserPreferences.getInstance(context.dataStore)
        val token = runBlocking {
            pref.getUserToken().first()
        }
        val apiService = ApiConfig.getApiService(token)
        return StoryRepository(database, apiService)

    }

    fun provideAuthRepository(context: Context): AuthRepository{
        val pref = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService, pref)
    }
}