package ru.practicum.shoppinglist.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListInteractorImpl @Inject constructor(
    val shoppingListRepository: ShoppingListRepository
) : ShoppingListInteractor {
    override suspend fun createShoppingList(shoppingList: ShoppingList) {
        return shoppingListRepository.createShoppingList(shoppingList)
    }

    override fun getShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListRepository.getShoppingLists()
    }
}