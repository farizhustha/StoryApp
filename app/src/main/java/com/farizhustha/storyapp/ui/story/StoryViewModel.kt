package com.farizhustha.storyapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.service.local.UserPreferences
import kotlinx.coroutines.launch

class StoryViewModel(private val pref: UserPreferences) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            pref.setToken("")
        }
    }
}