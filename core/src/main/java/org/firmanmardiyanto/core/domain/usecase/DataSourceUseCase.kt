package org.firmanmardiyanto.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.firmanmardiyanto.core.domain.model.User

interface DataSourceUseCase {
    fun userPreference(): Flow<User?>
    suspend fun updateUserPreference(user: User?)
}