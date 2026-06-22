package ru.practicum.shoppinglist.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.Resource
import ru.practicum.shoppinglist.domain.model.User
import ru.practicum.shoppinglist.domain.repository.AuthInteractor
import ru.practicum.shoppinglist.domain.repository.AuthRepository
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(
    private val repository: AuthRepository
) : AuthInteractor {
    override fun login(
        email: String,
        password: String
    ): Flow<Resource<User?>> {
        return repository.login(email, password)
    }

    override fun registration(
        email: String,
        password: String
    ): Flow<Resource<User?>> {
        return repository.registration(email, password)
    }

    override fun recoverPassword(email: String): Flow<Resource<String?>> {
        return repository.recoverPassword(email)
    }

    override fun checkToken(accessToken: String): Flow<Resource<CheckToken?>> {
        return repository.checkToken(accessToken)
    }

    override fun refreshToken(refreshToken: String): Flow<Resource<RefreshToken?>> {
        return repository.refreshToken(refreshToken)
    }
}