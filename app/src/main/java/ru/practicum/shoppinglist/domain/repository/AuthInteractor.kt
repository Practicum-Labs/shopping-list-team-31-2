package ru.practicum.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.Resource
import ru.practicum.shoppinglist.domain.model.User

interface AuthInteractor {
    fun login(email: String, password: String): Flow<Resource<User?>>
    fun registration(email: String, password: String): Flow<Resource<User?>>
    fun recoverPassword(email: String): Flow<Resource<String?>>
    fun checkToken(accessToken: String): Flow<Resource<CheckToken?>>
    fun refreshToken(refreshToken: String): Flow<Resource<RefreshToken?>>
//    fun getUserById(userId: String): User
//    fun getUserByAccessToken(accessToken: String): User
}