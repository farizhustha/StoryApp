package com.farizhustha.storyapp.ui.story.add

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farizhustha.storyapp.data.StoryRepository
import com.farizhustha.storyapp.model.AddStory
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _file = MutableLiveData<File>()
    val file: LiveData<File> = _file

    private val _location = MutableLiveData<Location>()
    val location : LiveData<Location> = _location

    fun addStory(photo: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            storyRepository.addStory(photo, description)
        }
    }

    fun addStoryWithLocation(item: AddStory){
        viewModelScope.launch {
            storyRepository.addStoryWithLocation(item)
        }
    }

    fun setFile(file: File) {
        _file.value = file
    }

    fun setLocation(location: Location){
        _location.value = location
    }
}