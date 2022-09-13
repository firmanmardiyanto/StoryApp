package org.firmanmardiyanto.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.firmanmardiyanto.core.data.Resource
import org.firmanmardiyanto.core.domain.model.User

interface IAuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(name: String,email: String, password: String): Flow<Resource<Boolean>>
}