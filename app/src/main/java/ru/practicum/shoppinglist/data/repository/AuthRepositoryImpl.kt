package ru.practicum.shoppinglist.data.repository

import ru.practicum.shoppinglist.data.network.api.AuthApiService
import ru.practicum.shoppinglist.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository