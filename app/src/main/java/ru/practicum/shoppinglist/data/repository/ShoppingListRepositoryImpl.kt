package ru.practicum.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.mapper.ShoppingListMapper
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao,
    private val slm: ShoppingListMapper
) : ShoppingListRepository {
    override suspend fun createShoppingList(shoppingList: ShoppingList): Long {
        val newId = dao.insertList(slm.convertShoppingListToEntity(shoppingList))
        return newId
    }

    override fun getShoppingLists(): Flow<List<ShoppingList>> {
        return dao.getAllLists()
            .map { entities ->
                slm.mapEntityListsToShoppingLists(entities)
            }
    }

    override suspend fun updateListIcon(id: Long, iconResId: Int) {
        dao.updateIcon(id = id, icon = iconResId)
    }

    override suspend fun delete() {
        return dao.delete()
    }
}