package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.domain.model.ShoppingList

data class ShoppingListState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val errorMessage: String? = null,
    val isListCreated: Boolean = false,
    val isIconAdded: Boolean = false
) {
    val isCreateClicked: Boolean
        get() = shoppingLists.isNotEmpty()
}