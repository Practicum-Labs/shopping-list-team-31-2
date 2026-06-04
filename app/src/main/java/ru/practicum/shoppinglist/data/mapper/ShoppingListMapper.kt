package ru.practicum.shoppinglist.data.mapper

import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity
import ru.practicum.shoppinglist.domain.model.ShoppingList

object ShoppingListMapper {
    fun convertShoppingListToEntity(shoppingList: ShoppingList) : ShoppingListEntity {
        return ShoppingListEntity(
            id = shoppingList.id,
            name = shoppingList.name,
            createdAt = shoppingList.createdAt,
            updatedAt = shoppingList.updatedAt
        )
    }

    fun convertEntityToShoppingList(shoppingListEntity: ShoppingListEntity) : ShoppingList {
        return ShoppingList(
            id = shoppingListEntity.id,
            name = shoppingListEntity.name,
            createdAt = shoppingListEntity.createdAt,
            updatedAt = shoppingListEntity.updatedAt
        )
    }

    fun mapEntityListsToShoppingLists(shoppingListsEntity: List<ShoppingListEntity>) : List<ShoppingList> {
        return shoppingListsEntity.map {shoppingList -> convertEntityToShoppingList(shoppingList)}
    }
}