package org.firmanmardiyanto.storyapp.poststory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.domain.usecase.StoryUseCase

class PostStoryViewModel(private val storyUseCase: StoryUseCase) : ViewModel() {
    fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ) =
        storyUseCase.postStory(photo, description, latitude, longitude).asLiveData()
}