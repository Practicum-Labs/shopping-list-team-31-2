package ru.practicum.shoppinglist.data.mapper

import ru.practicum.shoppinglist.data.local.entities.UserEntity
import ru.practicum.shoppinglist.data.network.model.response.CheckResponse
import ru.practicum.shoppinglist.data.network.model.response.LoginResponse
import ru.practicum.shoppinglist.data.network.model.response.RefreshTokenResponse
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.User
import ru.practicum.shoppinglist.util.Session
import javax.inject.Inject

class AuthMapper @Inject constructor() {
    fun loginResponseToUser(dto: LoginResponse): UserEntity {
        return UserEntity(
            userId = dto.userId,
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
            lastTokenUpdate = System.currentTimeMillis() + Session.SESSION_MS
        )
    }

    fun refreshTokenResponseToRefreshToken(dto: RefreshTokenResponse): RefreshToken {
        return RefreshToken(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken
        )
    }

    fun checkResponseToCheckToken(dto: CheckResponse): CheckToken {
        return CheckToken(
            isValid = dto.isValid,
            success = dto.success
        )
    }

    fun mapEntityToUser(entity: UserEntity): User {
        return User(
            userId = entity.userId,
            accessToken = entity.accessToken,
            refreshToken = entity.refreshToken,
            lastUpdateToken = entity.lastTokenUpdate
        )
    }

    fun mapUserToEntity(user: User): UserEntity {
        return UserEntity(
            userId = user.userId,
            accessToken = user.accessToken,
            refreshToken = user.refreshToken,
            lastTokenUpdate = user.lastUpdateToken
        )
    }

}