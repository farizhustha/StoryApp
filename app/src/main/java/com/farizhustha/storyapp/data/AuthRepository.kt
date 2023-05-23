package com.farizhustha.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.farizhustha.storyapp.model.User
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.service.remote.ApiService
import org.json.JSONObject
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService, private val pref: UserPreferences) {
    fun login(user: User): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(user.email, user.password)
            val loginResult = response.loginResult
            pref.setToken(loginResult?.token ?: "")
            emit(Result.Success("Login Success"))

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                JSONObject(it).getString("message")
            }
            emit(Result.Error(errorMessage ?: "Error"))
        }
    }

    fun register(user: User): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            apiService.register(user.name.toString(), user.email, user.password)
            emit(Result.Success("Register Successfully"))

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                JSONObject(it).getString("message")
            }
            emit(Result.Error(errorMessage ?: "Error"))
        }
    }

    fun getUserToken() = pref.getUserToken().asLiveData()

}