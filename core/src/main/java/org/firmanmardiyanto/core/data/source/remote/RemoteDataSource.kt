package org.firmanmardiyanto.core.data.source.remote

import android.content.Context
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.R
import org.firmanmardiyanto.core.data.source.remote.network.ApiResponse
import org.firmanmardiyanto.core.data.source.remote.network.ApiService
import org.firmanmardiyanto.core.data.source.remote.response.ListStoryResponse
import org.firmanmardiyanto.core.data.source.remote.response.LoginResponse
import org.firmanmardiyanto.core.data.source.remote.response.PostNewStoryResponse
import org.firmanmardiyanto.core.data.source.remote.response.RegisterResponse
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException


class RemoteDataSource(private val apiService: ApiService, private val context: Context) {
    private fun <T> parseError(
        response: Response<T>,
        messageKey: String = "message"
    ): String {
        return try {
            response.errorBody()?.string().runCatching {
                this?.let { JSONObject(it).getString(messageKey) }
            }.getOrNull() ?: response.message()
        } catch (e: IOException) {
            e.message ?: context.getString(R.string.unknow_error)
        }
    }

    suspend fun login(email: String, password: String): ApiResponse<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(parseError(response))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message!!)
        }
    }

    suspend fun getStories(
        withLocation: Boolean = false,
        page: Int,
        size: Int
    ): ApiResponse<ListStoryResponse> {
        return try {
            val response = apiService.getStories(
                if (withLocation) 1 else 0,
                page,
                size
            )
            if (response.isSuccessful) {
                val dataArray = response.body()!!.listStory
                if (dataArray.isNotEmpty()) {
                    ApiResponse.Success(response.body()!!)
                } else {
                    ApiResponse.Empty
                }
            } else {
                ApiResponse.Error(parseError(response))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message.toString())
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ApiResponse<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(parseError(response))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message.toString())
        }
    }

    suspend fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): ApiResponse<PostNewStoryResponse> {
        return try {
            val response = apiService.postStory(photo, description, latitude, longitude)
            if (response.isSuccessful) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(parseError(response))
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message.toString())
        }
    }
}
