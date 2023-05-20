package com.farizhustha.storyapp.service.remote.response

import com.google.gson.annotations.SerializedName

data class GetDetailStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: Story? = null
)