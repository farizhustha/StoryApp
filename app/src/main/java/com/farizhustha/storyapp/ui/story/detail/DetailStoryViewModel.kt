package com.farizhustha.storyapp.ui.story.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.GetDetailStoryResponse
import com.farizhustha.storyapp.service.remote.response.Story
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    init {
        getUserToken()
    }


    private fun getUserToken() {
        viewModelScope.launch {
            pref.getUserToken().collect { authToken ->
                _token.value = authToken
            }
        }

    }

    fun getDetailStory(token: String, id: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService(token).getDetailStory(id)
        client.enqueue(object : Callback<GetDetailStoryResponse> {
            override fun onResponse(
                call: Call<GetDetailStoryResponse>, response: Response<GetDetailStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.story as Story
                }
            }

            override fun onFailure(call: Call<GetDetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}