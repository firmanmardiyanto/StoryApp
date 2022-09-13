package org.firmanmardiyanto.storyapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.firmanmardiyanto.core.domain.model.User
import org.firmanmardiyanto.core.domain.usecase.AuthUseCase
import org.firmanmardiyanto.core.domain.usecase.DataSourceUseCase

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val dataSourceUseCase: DataSourceUseCase
) : ViewModel() {
    fun login(email: String, password: String) = authUseCase.login(email, password).asLiveData()
    fun register(name: String, email: String, password: String) =
        authUseCase.register(name, email, password).asLiveData()
    fun saveSession(user: User) =
        viewModelScope.launch { dataSourceUseCase.updateUserPreference(user) }
    fun logout() = viewModelScope.launch { dataSourceUseCase.updateUserPreference(null) }
    fun session() = dataSourceUseCase.userPreference().asLiveData()
}