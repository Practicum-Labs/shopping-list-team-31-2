package ru.practicum.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.shoppinglist.data.local.dao.UsersDao
import ru.practicum.shoppinglist.data.mapper.AuthMapper
import ru.practicum.shoppinglist.data.model.NetworkState
import ru.practicum.shoppinglist.data.network.api.NetworkClient
import ru.practicum.shoppinglist.data.network.model.NetworkResponse
import ru.practicum.shoppinglist.data.network.model.request.LoginRequest
import ru.practicum.shoppinglist.data.network.model.response.CheckResponse
import ru.practicum.shoppinglist.data.network.model.response.LoginResponse
import ru.practicum.shoppinglist.data.network.model.response.RefreshTokenResponse
import ru.practicum.shoppinglist.domain.model.CheckToken
import ru.practicum.shoppinglist.domain.model.RefreshToken
import ru.practicum.shoppinglist.domain.model.Resource
import ru.practicum.shoppinglist.domain.model.User
import ru.practicum.shoppinglist.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val mapper: AuthMapper,
    private val dao: UsersDao
) : AuthRepository {
    override fun login(
        email: String, password: String
    ): Flow<Resource<User?>> = flow {
        val response =
            networkClient.doRequestLogin(user = LoginRequest(email = email, password = password))
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                emit(Resource.Error(message = NetworkState.NoConnection.value))
            }

            NetworkResponse.OK_RESULT -> {
                val authorizationResponse = response.data as LoginResponse
                val mappedResponse = mapper.loginResponseToUser(authorizationResponse)
                emit(Resource.Success(mapper.mapEntityToUser(mappedResponse)))
            }

            NetworkResponse.BAD_REQUEST -> {
                when (response.data as String) {
                    NetworkState.IncorrectEmail.value -> {
                        emit(Resource.Error(message = NetworkState.IncorrectEmail.value))
                    }

                    NetworkState.ShortPassword.value -> {
                        emit(Resource.Error(message = NetworkState.ShortPassword.value))
                    }

                    else -> {
                        emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                    }
                }
            }

            NetworkResponse.UNAUTHORIZED -> {
                if (response.data as String == NetworkState.Unauthorized.value) {
                    emit(Resource.Error(message = NetworkState.Unauthorized.value))
                } else {
                    emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                }
            }

            else -> {
                emit(Resource.Error(message = NetworkState.UnexpectedError.value))
            }
        }
    }

    override fun registration(
        email: String, password: String
    ): Flow<Resource<User?>> = flow {
        val response = networkClient.doRequestRegistration(user = LoginRequest(email = email, password = password))
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                emit(Resource.Error(message = NetworkState.NoConnection.value))
            }

            NetworkResponse.OK_RESULT -> {
                val registrationResponse = response.data as LoginResponse
                val registrationUser = mapper.loginResponseToUser(registrationResponse)
                dao.registrationUser(registrationUser)
                emit(Resource.Success(mapper.mapEntityToUser(registrationUser)))
            }

            NetworkResponse.BAD_REQUEST -> {
                when (response.data as String) {
                    NetworkState.IncorrectEmail.value -> {
                        emit(Resource.Error(message = NetworkState.IncorrectEmail.value))
                    }

                    NetworkState.ShortPassword.value -> {
                        emit(Resource.Error(message = NetworkState.ShortPassword.value))
                    }

                    else -> {
                        emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                    }
                }
            }

            NetworkResponse.CONFLICT -> {
                if (response.data as String == NetworkState.Conflict.value) {
                    emit(Resource.Error(message = NetworkState.Conflict.value))
                } else {
                    emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                }

            }

            else -> {
                emit(Resource.Error(message = NetworkState.UnexpectedError.value))
            }
        }
    }

    override fun recoverPassword(email: String): Flow<Resource<String?>> = flow {
        val response = networkClient.doRequestRecovery(email = email)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                emit(Resource.Error(message = NetworkState.NoConnection.value))
            }

            NetworkResponse.OK_RESULT -> {
                val recoverResponse = response.data as String
                emit(Resource.Success(recoverResponse))
            }

            else -> {
                emit(Resource.Error(message = NetworkState.UnexpectedError.value))
            }
        }
    }

    override fun checkToken(accessToken: String): Flow<Resource<CheckToken?>> = flow {
        val response = networkClient.doRequestCheck(accessToken = accessToken)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                emit(Resource.Error(message = NetworkState.NoConnection.value))
            }

            NetworkResponse.OK_RESULT -> {
                val checkResponse = response.data as CheckResponse
                emit(Resource.Success(mapper.checkResponseToCheckToken(checkResponse)))
            }

            NetworkResponse.UNAUTHORIZED -> {
                if (response.data as String == NetworkState.Unauthorized.value) {
                    emit(Resource.Error(message = NetworkState.Unauthorized.value))
                } else {
                    emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                }
            }

            else -> {
                emit(Resource.Error(message = NetworkState.UnexpectedError.value))
            }
        }
    }

    override fun refreshToken(refreshToken: String): Flow<Resource<RefreshToken?>> = flow {
        val response = networkClient.doRequestRefresh(refreshToken = refreshToken)
        when (response.resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                emit(Resource.Error(message = NetworkState.NoConnection.value))
            }

            NetworkResponse.OK_RESULT -> {
                val refreshResponse = response.data as RefreshTokenResponse
                val mapRefreshToken = mapper.refreshTokenResponseToRefreshToken(refreshResponse)
                val user = dao.getUserByRefreshToken(refreshToken)
                user.refreshToken = mapRefreshToken.refreshToken
                user.accessToken = mapRefreshToken.accessToken
                dao.updateUser(user)
                emit(Resource.Success(mapRefreshToken))
            }

            NetworkResponse.UNAUTHORIZED -> {
                if (response.data as String == NetworkState.Unauthorized.value) {
                    emit(Resource.Error(message = NetworkState.Unauthorized.value))
                } else {
                    emit(Resource.Error(message = NetworkState.UnexpectedError.value))
                }
            }

            else -> {
                emit(Resource.Error(message = NetworkState.UnexpectedError.value))
            }
        }
    }

    private fun getUserById(userId: Long): User {
        return mapper.mapEntityToUser(dao.getUserById(userId))
    }

    private fun getUserByAccessToken(accessToken: String) : User {
        return mapper.mapEntityToUser(dao.getUserByAccessToken(accessToken))
    }
}