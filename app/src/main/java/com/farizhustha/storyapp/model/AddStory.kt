package com.farizhustha.storyapp.model

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AddStory(
    val photo: MultipartBody.Part,
    val description: RequestBody,
    val lat: RequestBody? = null,
    val lon: RequestBody? = null
)
