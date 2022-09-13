package org.firmanmardiyanto.core.domain.model

import android.os.Parcelable
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    val name: String,
    val token: String
) : Parcelable

object UserPreferenceKeys {
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_TOKEN = stringPreferencesKey("user_token")
}
