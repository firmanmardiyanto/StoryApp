package org.firmanmardiyanto.storyapp.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.domain.usecase.StoryUseCase

class MapsViewModel(private val storyUseCase: StoryUseCase) : ViewModel() {
    val stories = MutableLiveData<Resource<List<Story>>>()

    fun fetchStories() {
        storyUseCase.getStories(true).asLiveData().observeForever {
            stories.value = it
        }
    }
}