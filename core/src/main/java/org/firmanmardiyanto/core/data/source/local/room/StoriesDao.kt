package org.firmanmardiyanto.core.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.firmanmardiyanto.core.data.source.remote.response.StoryResponse
import org.firmanmardiyanto.core.domain.model.Story

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(story: List<StoryResponse>)

    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, Story>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}