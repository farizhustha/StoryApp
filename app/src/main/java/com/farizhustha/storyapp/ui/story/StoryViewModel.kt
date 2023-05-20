package com.farizhustha.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.GeneralResponse
import com.farizhustha.storyapp.service.remote.response.GetAllStoriesResponse
import com.farizhustha.storyapp.service.remote.response.Story
import com.farizhustha.storyapp.utils.SingleEvent
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _message = MutableLiveData<SingleEvent<String>>()
    val message: LiveData<SingleEvent<String>> = _message

    private val _file = MutableLiveData<File>()
    val file: LiveData<File> = _file

    init {
        getUserToken()
    }

    private fun getUserToken() {
        viewModelScope.launch {
            pref.getUserToken().collect { authToken ->
                _token.value = authToken
                getAllStories(authToken)
            }

        }
    }

    private fun getAllStories(token: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService(token).getAllStories(0)
        client.enqueue(object : Callback<GetAllStoriesResponse> {
            override fun onResponse(
                call: Call<GetAllStoriesResponse>, response: Response<GetAllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listStory.value = response.body()?.listStory as List<Story>
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let {
                        JSONObject(it).getString("message")
                    }
                    _message.value = SingleEvent(errorMessage ?: "Error")
                }
            }

            override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = SingleEvent("Error")
            }
        })

    }

    fun setFile(file: File) {
        _file.value = file
    }

    fun addStory(token: String, photo: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true

        val client = ApiConfig.getApiService(token).addStory(photo, description)
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>, response: Response<GeneralResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _message.value = SingleEvent(response.body()?.message.toString())
                    getAllStories(token)
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let {
                        JSONObject(it).getString("message")
                    }
                    _message.value = SingleEvent(errorMessage ?: "Error")
                }

            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = SingleEvent("Error")
            }
        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.setToken("")
        }
    }
}