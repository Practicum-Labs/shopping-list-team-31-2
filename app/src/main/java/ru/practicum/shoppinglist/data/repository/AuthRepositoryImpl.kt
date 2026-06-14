package ru.practicum.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.shoppinglist.data.network.api.NetworkClient
import ru.practicum.shoppinglist.data.network.model.NetworkResponse
import ru.practicum.shoppinglist.data.network.model.response.LoginResponse
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.Resource
import ru.practicum.shoppinglist.domain.model.User
import ru.practicum.shoppinglist.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient
) : AuthRepository {
    override fun login(
        email: String,
        password: String
    ): Flow<Resource<Pair<User?, Int>?>> = flow {
        val response = networkClient.doRequestLogin(email = email, password = password)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION-> {}

            NetworkResponse.OK_RESULT -> {}

            NetworkResponse.BAD_REQUEST -> {}

            NetworkResponse.UNAUTHORIZED -> {}

            NetworkResponse.CONFLICT -> {}

            NetworkResponse.INTERNAL_SERVER_ERROR -> {}
        }
    }

    override fun registration(
        email: String,
        password: String
    ): Flow<Resource<Pair<User?, Int>?>> = flow {
        val response = networkClient.doRequestRegistration(email = email, password = password)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {}

            NetworkResponse.OK_RESULT -> {
                val registrationResponse = response.data as LoginResponse

            }

            NetworkResponse.BAD_REQUEST -> {}

            NetworkResponse.UNAUTHORIZED -> {}

            NetworkResponse.CONFLICT -> {}

            NetworkResponse.INTERNAL_SERVER_ERROR -> {}
        }
    }

    override fun recoverPassword(email: String): Flow<Resource<Pair<String?, Int>?>> = flow {
        val response = networkClient.doRequestRecovery(email = email)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {}

            NetworkResponse.OK_RESULT -> {}

            NetworkResponse.BAD_REQUEST -> {}

            NetworkResponse.UNAUTHORIZED -> {}

            NetworkResponse.CONFLICT -> {}

            NetworkResponse.INTERNAL_SERVER_ERROR -> {}
        }
    }

    override fun checkToken(accessToken: String): Flow<Resource<Pair<CheckToken?, Int>?>> = flow {
        val response = networkClient.doRequestCheck(accessToken = accessToken)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {}

            NetworkResponse.OK_RESULT -> {}

            NetworkResponse.BAD_REQUEST -> {}

            NetworkResponse.UNAUTHORIZED -> {}

            NetworkResponse.CONFLICT -> {}

            NetworkResponse.INTERNAL_SERVER_ERROR -> {}
        }
    }

    override fun refreshToken(refreshToken: String): Flow<Resource<Pair<RefreshToken?, Int>?>> = flow {
        val response = networkClient.doRequestRefresh(refreshToken = refreshToken)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {}

            NetworkResponse.OK_RESULT -> {}

            NetworkResponse.BAD_REQUEST -> {}

            NetworkResponse.UNAUTHORIZED -> {}

            NetworkResponse.CONFLICT -> {}

            NetworkResponse.INTERNAL_SERVER_ERROR -> {}
        }
    }
}