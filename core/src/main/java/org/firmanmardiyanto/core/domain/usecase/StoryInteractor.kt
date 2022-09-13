package org.firmanmardiyanto.core.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.domain.repository.IStoryRepository

class StoryInteractor(private val storyRepository: IStoryRepository) : StoryUseCase {
    override fun getStories(withLocation: Boolean): Flow<Resource<List<Story>>> =
        storyRepository.getStories(withLocation)

    override fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<Boolean>> = storyRepository.postStory(photo, description, latitude, longitude)

    override fun getPagingStories(): Flow<PagingData<Story>> =
        storyRepository.getPagingStories()
}