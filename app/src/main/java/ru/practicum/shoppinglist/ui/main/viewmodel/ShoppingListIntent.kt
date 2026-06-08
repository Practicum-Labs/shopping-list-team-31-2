package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.domain.model.ShoppingList

sealed interface ShoppingListIntent {
    data class AddShoppingList(val shoppingList: ShoppingList) : ShoppingListIntent
    data class SetAddedName(val addedName: String) : ShoppingListIntent
    data class SetAddedId(val addedId: Long) : ShoppingListIntent
    data class SetAddedIcon(val addedIcon: Int) : ShoppingListIntent
    data object DeleteAll : ShoppingListIntent
    data object GetAllShoppingList : ShoppingListIntent
}