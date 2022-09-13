package org.firmanmardiyanto.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.model.User
import org.firmanmardiyanto.core.domain.repository.IAuthRepository

class AuthInteractor(private val authRepository: IAuthRepository) : AuthUseCase {
    override fun login(email: String, password: String): Flow<Resource<User>> =
        authRepository.login(email, password)

    override fun register(name: String, email: String, password: String): Flow<Resource<Boolean>> =
        authRepository.register(name, email, password)
}
