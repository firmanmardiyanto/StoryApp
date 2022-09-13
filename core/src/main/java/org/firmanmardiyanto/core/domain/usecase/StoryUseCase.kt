package org.firmanmardiyanto.core.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.model.Story

interface StoryUseCase {
    fun getStories(withLocation: Boolean = false): Flow<Resource<List<Story>>>
    fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<Boolean>>
    fun getPagingStories(): Flow<PagingData<Story>>
}