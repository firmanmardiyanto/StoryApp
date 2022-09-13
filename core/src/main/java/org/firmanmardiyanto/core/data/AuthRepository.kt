package org.firmanmardiyanto.core.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.firmanmardiyanto.core.R
import org.firmanmardiyanto.core.data.source.remote.RemoteDataSource
import org.firmanmardiyanto.core.data.source.remote.network.ApiResponse
import org.firmanmardiyanto.core.domain.model.User
import org.firmanmardiyanto.core.domain.repository.IAuthRepository
import org.firmanmardiyanto.core.utils.DataMapper

class AuthRepository(private val remoteDataSource: RemoteDataSource, private val context: Context) :
    IAuthRepository {
    override fun login(email: String, password: String): Flow<Resource<User>> =
        flow {
            emit(Resource.Loading())
            when (val result = remoteDataSource.login(email, password)) {
                is ApiResponse.Success -> emit(
                    Resource.Success(
                        DataMapper.mapUserResponseToDomain(
                            result.data.loginResult
                        )
                    )
                )
                is ApiResponse.Error -> emit(Resource.Error(result.errorMessage))
                else -> {
                    emit(Resource.Error(context.getString(R.string.unknow_error)))
                }
            }
        }.flowOn(Dispatchers.IO)

    override fun register(name: String, email: String, password: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            when (val result = remoteDataSource.register(name, email, password)) {
                is ApiResponse.Success -> emit(Resource.Success(true))
                is ApiResponse.Error -> emit(Resource.Error(result.errorMessage))
                else -> {
                    emit(Resource.Error(context.getString(R.string.unknow_error)))
                }
            }
        }.flowOn(Dispatchers.IO)
}