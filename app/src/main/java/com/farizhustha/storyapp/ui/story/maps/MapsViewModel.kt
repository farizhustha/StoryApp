package com.farizhustha.storyapp.ui.story.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.GetAllStoriesResponse
import com.farizhustha.storyapp.service.remote.response.Story
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
        _isLoading.value = true

        val client = ApiConfig.getApiService(token).getAllStories(1)
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>, response: Response<GetAllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory as List<Story>
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })

    }
}