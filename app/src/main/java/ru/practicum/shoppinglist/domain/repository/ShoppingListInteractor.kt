package ru.practicum.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.ShoppingList

interface ShoppingListInteractor {
    suspend fun createShoppingList(shoppingList: ShoppingList): Long
    fun getShoppingListsByUserId(userId: Long): Flow<List<ShoppingList>>
    suspend fun updateListIcon(id: Long, iconResId: Int)
    suspend fun deleteAllListsByUserId(userId: Long)
    suspend fun renameList(id: Long, newName: String)
    suspend fun deleteListById(id: Long)
}