package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.core.mvi.MviState
import ru.practicum.shoppinglist.domain.model.ShoppingList

data class ShoppingListState(
    val shoppingLists: MutableList<ShoppingList> = mutableListOf<ShoppingList>(),
    val errorMessage: String? = null,
    val isListCreated: Boolean = false,
    val isIconAdded: Boolean = false
) : MviState {
    val isCreateClicked: Boolean
        get() = shoppingLists.isNotEmpty()
}