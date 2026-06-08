package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.domain.model.ShoppingList

sealed interface ShoppingListIntent {
    data class AddShoppingList(val shoppingList: ShoppingList) : ShoppingListIntent
    data object DeleteAll : ShoppingListIntent
    data object GetAllShoppingList : ShoppingListIntent
}