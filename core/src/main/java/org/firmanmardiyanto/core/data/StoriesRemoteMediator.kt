package org.firmanmardiyanto.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import org.firmanmardiyanto.core.data.source.local.LocalDataSource
import org.firmanmardiyanto.core.data.source.local.room.RemoteKeys
import org.firmanmardiyanto.core.data.source.remote.RemoteDataSource
import org.firmanmardiyanto.core.data.source.remote.network.ApiResponse
import org.firmanmardiyanto.core.domain.model.Story

@OptIn(ExperimentalPagingApi::class)
class StoriesRemoteMediator(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : RemoteMediator<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            when (val responseData =
                remoteDataSource.getStories(false, page, state.config.pageSize)) {
                is ApiResponse.Success -> {
                    val endOfPaginationReached = responseData.data.listStory.isEmpty()

                    if (loadType == LoadType.REFRESH) {
                        localDataSource.deleteRemoteKey()
                        localDataSource.deleteStories()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.data.listStory.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    keys.let { localDataSource.insertRemoteKey(it) }
                    responseData.data.listStory.let {
                        localDataSource.insertStories(it)
                    }
                    return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
                is ApiResponse.Error -> {
                    return MediatorResult.Error(Error(responseData.errorMessage))
                }
                is ApiResponse.Empty -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            localDataSource.getRemoteKey(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            localDataSource.getRemoteKey(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                localDataSource.getRemoteKey(id)
            }
        }
    }

}