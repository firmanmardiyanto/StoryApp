package org.firmanmardiyanto.core.data.source.local

import org.firmanmardiyanto.core.data.source.local.room.RemoteKeys
import org.firmanmardiyanto.core.data.source.local.room.RemoteKeysDao
import org.firmanmardiyanto.core.data.source.local.room.StoriesDao
import org.firmanmardiyanto.core.data.source.remote.response.StoryResponse

class LocalDataSource(private val storiesDao: StoriesDao, private val remoteKeys: RemoteKeysDao) {
    fun getStories() = storiesDao.getStories()
    suspend fun insertStories(stories: List<StoryResponse>) = storiesDao.insertStories(stories)
    suspend fun deleteStories() = storiesDao.deleteAll()
    suspend fun getRemoteKey(storyId: String) = remoteKeys.getRemoteKeysId(storyId)
    suspend fun insertRemoteKey(remoteKey: List<RemoteKeys>) = remoteKeys.insertAll(remoteKey)
    suspend fun deleteRemoteKey() = remoteKeys.deleteRemoteKeys()
}