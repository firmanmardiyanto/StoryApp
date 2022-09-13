package org.firmanmardiyanto.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.firmanmardiyanto.core.data.source.remote.response.StoryResponse

@Database(
    entities = [StoryResponse::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoriesDatabase : RoomDatabase() {
    abstract fun storiesDao(): StoriesDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}