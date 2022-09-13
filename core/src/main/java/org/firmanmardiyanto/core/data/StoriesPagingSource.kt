package org.firmanmardiyanto.core.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.firmanmardiyanto.core.data.source.remote.RemoteDataSource
import org.firmanmardiyanto.core.data.source.remote.network.ApiResponse
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.utils.DataMapper

class StoriesPagingSource(private val remoteDataSource: RemoteDataSource) :
    PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            when (
                val responseData =
                    remoteDataSource.getStories(false, page, params.loadSize)) {
                is ApiResponse.Success -> {
                    val response = responseData.data
                    val stories = DataMapper.mapStoryResponsesToDomain(response.listStory)
                    return LoadResult.Page(
                        data = stories,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (stories.isEmpty()) null else page + 1
                    )
                }
                is ApiResponse.Error -> {
                    return LoadResult.Error(Error(responseData.errorMessage))
                }
                is ApiResponse.Empty -> {
                    return LoadResult.Error(Error("Empty response"))
                }
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}