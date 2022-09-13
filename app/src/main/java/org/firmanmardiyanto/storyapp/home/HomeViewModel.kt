package org.firmanmardiyanto.storyapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.domain.usecase.StoryUseCase

class HomeViewModel(storyUseCase: StoryUseCase) : ViewModel() {
    val storiesPaging: LiveData<PagingData<Story>> =
        storyUseCase.getPagingStories().cachedIn(viewModelScope).asLiveData()
}