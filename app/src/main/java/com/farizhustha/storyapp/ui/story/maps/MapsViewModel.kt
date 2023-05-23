package com.farizhustha.storyapp.ui.story.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.Story
import kotlinx.coroutines.launch

class MapsViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    init {
        getUserToken()
    }

    private fun getUserToken() {
        viewModelScope.launch {
            pref.getUserToken().collect { authToken ->
                getAllStoriesWithLocation(authToken)
            }

        }
    }

    private fun getAllStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService(token).getAllStories(1)
                val stories = response.listStory
                stories.let {
                    _listStory.value = it
                }
            } catch (_: Exception) {
            }
        }
    }
}