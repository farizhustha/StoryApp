package com.farizhustha.storyapp.ui.story.detail

import androidx.lifecycle.ViewModel
import com.farizhustha.storyapp.data.StoryRepository

class DetailStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getDetailStory(id: String) = storyRepository.getDetailStory(id)

}