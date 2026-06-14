package ru.practicum.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.Resource
import ru.practicum.shoppinglist.domain.model.User

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<Pair<User?, Int>?>>
    fun registration(email: String, password: String): Flow<Resource<Pair<User?, Int>?>>
    fun recoverPassword(email: String): Flow<Resource<Pair<String?, Int>?>>
    fun checkToken(accessToken: String): Flow<Resource<Pair<CheckToken?, Int>?>>
    fun refreshToken(refreshToken: String): Flow<Resource<Pair<RefreshToken?, Int>?>>
}