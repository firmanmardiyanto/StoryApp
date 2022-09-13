package org.firmanmardiyanto.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.firmanmardiyanto.core.domain.model.User

interface IDataSourceRepository {
    fun userPreference(): Flow<User?>
    suspend fun updateUserPreference(user: User?)
}