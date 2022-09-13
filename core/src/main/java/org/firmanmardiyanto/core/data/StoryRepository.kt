package org.firmanmardiyanto.core.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.firmanmardiyanto.core.R
import org.firmanmardiyanto.core.data.source.local.LocalDataSource
import org.firmanmardiyanto.core.data.source.remote.RemoteDataSource
import org.firmanmardiyanto.core.data.source.remote.network.ApiResponse
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.domain.repository.IStoryRepository
import org.firmanmardiyanto.core.utils.DataMapper

class StoryRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val context: Context
) : IStoryRepository {
    override fun getStories(withLocation: Boolean): Flow<Resource<List<Story>>> =
        flow {
            emit(Resource.Loading())
            when (val result = remoteDataSource.getStories(withLocation, 1, 10)) {
                is ApiResponse.Success -> emit(
                    Resource.Success(DataMapper.mapStoryResponsesToDomain(result.data.listStory))
                )
                is ApiResponse.Error -> emit(Resource.Error(result.errorMessage))
                else -> {
                    emit(Resource.Error(context.getString(R.string.unknow_error)))
                }
            }
        }.flowOn(Dispatchers.IO)

    override fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        when (val result = remoteDataSource.postStory(photo, description, latitude, longitude)) {
            is ApiResponse.Success -> emit(Resource.Success(true))
            is ApiResponse.Error -> emit(Resource.Error(result.errorMessage))
            else -> {
                emit(Resource.Error(context.getString(R.string.unknow_error)))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getPagingStories(): Flow<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoriesRemoteMediator(
                localDataSource,
                remoteDataSource
            ),
            pagingSourceFactory = {
                StoriesPagingSource(
                    remoteDataSource,
                )
                localDataSource.getStories()
            }
        ).flow
    }
}