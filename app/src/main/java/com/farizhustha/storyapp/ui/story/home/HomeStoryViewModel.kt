package com.farizhustha.storyapp.ui.story.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.farizhustha.storyapp.data.StoryRepository
import com.farizhustha.storyapp.service.remote.response.Story

class HomeStoryViewModel(storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}