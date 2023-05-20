package com.farizhustha.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.farizhustha.storyapp.service.local.UserPreferences

class MainViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getUserToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }
}