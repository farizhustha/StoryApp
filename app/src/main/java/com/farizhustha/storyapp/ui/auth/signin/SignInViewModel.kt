package com.farizhustha.storyapp.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.model.User
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiConfig
import com.farizhustha.storyapp.service.remote.response.LoginResponse
import com.farizhustha.storyapp.utils.SingleEvent
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val pref: UserPreferences) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<SingleEvent<String>>()
    val message: LiveData<SingleEvent<String>> = _message

    fun getUserToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }

    fun login(user: User) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().login(user.email, user.password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _message.value = SingleEvent("Login Success")
                    viewModelScope.launch {
                        pref.setToken(response.body()?.loginResult?.token ?: "")
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = errorResponse?.let {
                        JSONObject(it).getString("message")
                    }
                    _message.value = SingleEvent(errorMessage ?: "Error")

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = SingleEvent("Error")
            }
        })
    }
}