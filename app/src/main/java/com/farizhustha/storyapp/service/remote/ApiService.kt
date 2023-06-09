package com.farizhustha.storyapp.service.remote

import com.farizhustha.storyapp.service.remote.response.GeneralResponse
import com.farizhustha.storyapp.service.remote.response.GetAllStoriesResponse
import com.farizhustha.storyapp.service.remote.response.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): GeneralResponse

    @GET("stories")
    suspend fun getAllStories(@Query("location") location: Int): GetAllStoriesResponse

    @GET("stories")
    suspend fun getStoriesWithPaging(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): GeneralResponse

    @Multipart
    @POST("stories")
    suspend fun addStoryWithLocation(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): GeneralResponse
}