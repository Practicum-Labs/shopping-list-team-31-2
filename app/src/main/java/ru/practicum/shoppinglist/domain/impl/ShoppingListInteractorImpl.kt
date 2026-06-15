package ru.practicum.shoppinglist.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListInteractorImpl @Inject constructor(
    val shoppingListRepository: ShoppingListRepository
) : ShoppingListInteractor {
    override suspend fun createShoppingList(shoppingList: ShoppingList): Long {
        return shoppingListRepository.createShoppingList(shoppingList)
    }

    override fun getShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListRepository.getShoppingLists()
    }

    override suspend fun updateListIcon(id: Long, iconResId: Int) {
        return shoppingListRepository.updateListIcon(id = id, iconResId = iconResId)
    }

    override suspend fun delete() {
        return shoppingListRepository.delete()
    }

    override suspend fun renameList(id: Long, newName: String) {
        return shoppingListRepository.renameList(id, newName)
    }

    override suspend fun deleteListById(id: Long) {
        return shoppingListRepository.deleteListById(id)
    }
}