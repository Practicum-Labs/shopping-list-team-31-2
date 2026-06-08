package ru.practicum.shoppinglist.data.mapper

import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity
import ru.practicum.shoppinglist.domain.model.ShoppingList
import javax.inject.Inject

class ShoppingListMapper @Inject constructor() {
    fun convertShoppingListToEntity(shoppingList: ShoppingList): ShoppingListEntity {
        return ShoppingListEntity(
            id = shoppingList.id,
            name = shoppingList.name,
            icon = shoppingList.icon,
        )
    }

    fun convertEntityToShoppingList(shoppingListEntity: ShoppingListEntity): ShoppingList {
        return ShoppingList(
            id = shoppingListEntity.id,
            name = shoppingListEntity.name,
            icon = shoppingListEntity.icon,
        )
    }

    fun mapEntityListsToShoppingLists(shoppingListsEntity: List<ShoppingListEntity>): List<ShoppingList> {
        return shoppingListsEntity.map { shoppingList -> convertEntityToShoppingList(shoppingList) }
    }
}
