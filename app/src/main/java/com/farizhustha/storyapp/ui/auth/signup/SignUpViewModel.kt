package com.farizhustha.storyapp.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.farizhustha.storyapp.model.User
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.GeneralResponse
import com.farizhustha.storyapp.utils.SingleEvent
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<SingleEvent<String>>()
    val message: LiveData<SingleEvent<String>> = _message

    fun register(user: User) {
        _isLoading.value = true

        val client =
            ApiConfig.getApiService().register(user.name.toString(), user.email, user.password)
        client.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>, response: Response<GeneralResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _message.value = SingleEvent("Register Successfully")
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
}