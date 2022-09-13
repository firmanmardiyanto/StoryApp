package org.firmanmardiyanto.core.data.source.remote.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.data.source.remote.response.ListStoryResponse
import org.firmanmardiyanto.core.data.source.remote.response.LoginResponse
import org.firmanmardiyanto.core.data.source.remote.response.PostNewStoryResponse
import org.firmanmardiyanto.core.data.source.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStories(
        @Query("location") withLocation: Int?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<ListStoryResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody?,
        @Part("lon") longitude: RequestBody?
    ): Response<PostNewStoryResponse>
}