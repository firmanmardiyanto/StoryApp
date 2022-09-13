package org.firmanmardiyanto.core.utils

import org.firmanmardiyanto.core.data.source.remote.response.LoginResultResponse
import org.firmanmardiyanto.core.data.source.remote.response.StoryResponse
import org.firmanmardiyanto.core.domain.model.Story
import org.firmanmardiyanto.core.domain.model.User

object DataMapper {
    fun mapStoryResponsesToDomain(input: List<StoryResponse>): List<Story> = input.map {
        Story(
            id = it.id,
            name = it.name,
            description = it.description,
            photoUrl = it.photoUrl,
            createdAt = it.createdAt,
            lat = it.lat,
            lon = it.lon
        )
    }

    fun mapUserResponseToDomain(input: LoginResultResponse): User = User(
        userId = input.userId,
        name = input.name,
        token = input.token
    )
}