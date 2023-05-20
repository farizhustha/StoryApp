package com.farizhustha.storyapp.service.remote.response

import com.google.gson.annotations.SerializedName

data class GetAllStoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<Story>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)