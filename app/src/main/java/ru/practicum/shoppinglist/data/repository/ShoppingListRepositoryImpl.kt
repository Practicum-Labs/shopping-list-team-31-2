package ru.practicum.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.mapper.ShoppingListMapper
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao,
    private val slm: ShoppingListMapper
) : ShoppingListRepository {
    override suspend fun createShoppingList(shoppingList: ShoppingList) {
        dao.insertList(slm.convertShoppingListToEntity(shoppingList))
    }

    override fun getShoppingLists(): Flow<List<ShoppingList>> = flow {
        val shoppingLists = dao.getAllLists()
        emit(slm.mapEntityListsToShoppingLists(shoppingLists))
    }
}