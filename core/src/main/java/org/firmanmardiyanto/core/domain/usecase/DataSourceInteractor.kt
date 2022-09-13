package org.firmanmardiyanto.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.firmanmardiyanto.core.domain.model.User
import org.firmanmardiyanto.core.domain.repository.IDataSourceRepository

class DataSourceInteractor(private val dataSourceRepository: IDataSourceRepository) :
    DataSourceUseCase {
    override fun userPreference(): Flow<User?> = dataSourceRepository.userPreference()

    override suspend fun updateUserPreference(user: User?) =
        dataSourceRepository.updateUserPreference(user)
}