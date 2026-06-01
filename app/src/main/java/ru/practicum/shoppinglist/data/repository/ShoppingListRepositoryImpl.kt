package ru.practicum.shoppinglist.data.repository

import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao
) : ShoppingListRepository