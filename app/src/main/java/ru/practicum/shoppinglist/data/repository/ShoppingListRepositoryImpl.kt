package ru.practicum.shoppinglist.data.repository

import javax.inject.Inject
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao
) : ShoppingListRepository {

}