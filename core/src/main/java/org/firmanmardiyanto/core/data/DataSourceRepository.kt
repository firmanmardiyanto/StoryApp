package org.firmanmardiyanto.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.firmanmardiyanto.core.domain.model.User
import org.firmanmardiyanto.core.domain.model.UserPreferenceKeys
import org.firmanmardiyanto.core.domain.repository.IDataSourceRepository
import java.io.IOException


private val Context.dataStore by preferencesDataStore(name = "data_store")

class DataSourceRepository(private val context: Context) : IDataSourceRepository {
    override fun userPreference(): Flow<User?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            val userId = preferences[UserPreferenceKeys.USER_ID]
            val name = preferences[UserPreferenceKeys.USER_NAME]
            val token = preferences[UserPreferenceKeys.USER_TOKEN]
            if (userId != null && name != null && token != null) User(userId, name, token) else null
        }

    override suspend fun updateUserPreference(user: User?) {
        context.dataStore.edit { preferences ->
            if (user != null) {
                preferences[UserPreferenceKeys.USER_ID] = user.userId
                preferences[UserPreferenceKeys.USER_NAME] = user.name
                preferences[UserPreferenceKeys.USER_TOKEN] = user.token
            } else {
                preferences.clear()
            }
        }
    }

}