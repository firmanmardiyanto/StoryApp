package org.firmanmardiyanto.storyapp.di

import org.firmanmardiyanto.core.domain.usecase.*
import org.firmanmardiyanto.storyapp.auth.AuthViewModel
import org.firmanmardiyanto.storyapp.home.HomeViewModel
import org.firmanmardiyanto.storyapp.maps.MapsViewModel
import org.firmanmardiyanto.storyapp.poststory.PostStoryViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<StoryUseCase> { StoryInteractor(get()) }
    factory<DataSourceUseCase> { DataSourceInteractor(get()) }
    factory<AuthUseCase> { AuthInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { MapsViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { PostStoryViewModel(get()) }
}