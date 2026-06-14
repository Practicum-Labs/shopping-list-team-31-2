package ru.practicum.shoppinglist.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.shoppinglist.data.network.api.AuthApiService
import ru.practicum.shoppinglist.data.network.api.NetworkClient
import ru.practicum.shoppinglist.data.network.model.NetworkResponse
import ru.practicum.shoppinglist.data.network.model.request.LoginRequest
import ru.practicum.shoppinglist.data.network.model.request.RefreshTokenRequest
import javax.inject.Inject

class NetworkClientImpl @Inject constructor (
    private val authApi: AuthApiService,
    @ApplicationContext private val context: Context
) : NetworkClient {
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isPreferredTransport = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        return hasInternet ?: false && isPreferredTransport
    }

    private suspend fun apiCall(
        doRequest: suspend () -> Any?
    ): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply { resultCode = NetworkResponse.NO_CONNECTION }
        }
        return withContext(Dispatchers.IO) {
            try {
                val result = doRequest()
                NetworkResponse().apply {
                    resultCode = NetworkResponse.OK_RESULT
                    data = result
                }
            } catch (ex: HttpException) {
                Log.e("error", "Ошибка: ${ex.message}")
                NetworkResponse().apply { resultCode = NetworkResponse.BAD_REQUEST }
            }
        }
    }

    override suspend fun doRequestLogin(
        user: LoginRequest
    ): NetworkResponse {
        return apiCall { authApi.login(user) }
    }

    override suspend fun doRequestRegistration(
        user: LoginRequest
    ): NetworkResponse {
        return apiCall { authApi.register(user) }
    }

    override suspend fun doRequestRecovery(email: String): NetworkResponse {
        return apiCall { authApi.recovery(email) }
    }

    override suspend fun doRequestRefresh(refreshToken: String): NetworkResponse {
        val request = RefreshTokenRequest(refreshToken)
        return apiCall { authApi.refreshToken(request) }
    }

    override suspend fun doRequestCheck(accessToken: String): NetworkResponse {
        return apiCall { authApi.checkAuth(accessToken) }
    }
}
