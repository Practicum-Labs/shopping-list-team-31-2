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
import ru.practicum.shoppinglist.util.Session
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val mapper: AuthMapper,
    private val dao: UsersDao
) : AuthRepository {
    override fun login(
        email: String,
        password: String
    ): Flow<Resource<User?>> = flow {
        val response = networkClient.doRequestLogin(
            LoginRequest(email, password)
        )
        val error = handleCommonError(response.resultCode)
        if (error != null) {
            emit(error)
        } else {
            when (response.resultCode) {
                NetworkResponse.OK_RESULT -> {
                    val data = response.data as LoginResponse
                    val entity = mapper.loginResponseToUser(data)
                    emit(
                        Resource.Success(
                            mapper.mapEntityToUser(entity)
                        )
                    )
                }
                NetworkResponse.BAD_REQUEST -> {
                    emit(handleBadRequest(response.data as String))
                }
                NetworkResponse.UNAUTHORIZED -> {
                    emit(handleUnauthorized(response.data as String))
                }
                else -> {
                    emit(
                        Resource.Error(
                            NetworkState.UnexpectedError.value
                        )
                    )
                }
            }
        }
    }

    override fun registration(
        email: String,
        password: String
    ): Flow<Resource<User?>> = flow {
        val response = networkClient.doRequestRegistration(
            LoginRequest(
                email = email,
                password = password
            )
        )
        val error = handleCommonError(response.resultCode)
        if (error != null) {
            emit(error)
        } else {
            when (response.resultCode) {
                NetworkResponse.OK_RESULT -> {
                    val data = response.data as LoginResponse
                    val userEntity =
                        mapper.loginResponseToUser(data)
                    dao.registrationUser(userEntity)
                    emit(
                        Resource.Success(
                            mapper.mapEntityToUser(userEntity)
                        )
                    )
                }

                NetworkResponse.BAD_REQUEST -> {
                    emit(
                        handleBadRequest(
                            response.data as String
                        )
                    )
                }

                NetworkResponse.CONFLICT -> {
                    emit(
                        handleConflict(
                            response.data as String
                        )
                    )
                }

                else -> {
                    emit(
                        Resource.Error(
                            NetworkState.UnexpectedError.value
                        )
                    )
                }
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
                val recoverResponse = response.data?.toString() ?: ""
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
                val user = mapper.mapEntityToUser(dao.getUserByRefreshToken(refreshToken))
                val updatedUser = user.copy(
                    refreshToken = mapRefreshToken.refreshToken,
                    accessToken = mapRefreshToken.accessToken,
                    lastUpdateToken = System.currentTimeMillis() + Session.SESSION_MS
                )
                dao.updateUser(mapper.mapUserToEntity(updatedUser))
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

    private fun getUserByAccessToken(accessToken: String): User {
        return mapper.mapEntityToUser(dao.getUserByAccessToken(accessToken))
    }

    private fun handleCommonError(
        resultCode: Int
    ): Resource.Error<Nothing>? {
        return when (resultCode) {
            NetworkResponse.NO_CONNECTION -> {
                Resource.Error(NetworkState.NoConnection.value)
            }

            else -> null
        }
    }

    private fun handleBadRequest(
        message: String
    ): Resource.Error<Nothing> {
        return when (message) {
            NetworkState.IncorrectEmail.value -> {
                Resource.Error(NetworkState.IncorrectEmail.value)
            }

            NetworkState.ShortPassword.value -> {
                Resource.Error(NetworkState.ShortPassword.value)
            }

            else -> {
                Resource.Error(NetworkState.UnexpectedError.value)
            }
        }
    }

    private fun handleUnauthorized(
        message: String
    ): Resource.Error<Nothing> {
        return if (message == NetworkState.Unauthorized.value) {
            Resource.Error(NetworkState.Unauthorized.value)
        } else {
            Resource.Error(NetworkState.UnexpectedError.value)
        }
    }

    private fun handleConflict(
        message: String
    ): Resource.Error<Nothing> {
        return if (message == NetworkState.Conflict.value) {
            Resource.Error(
                NetworkState.Conflict.value
            )

        } else {
            Resource.Error(
                NetworkState.UnexpectedError.value
            )
        }
    }

}