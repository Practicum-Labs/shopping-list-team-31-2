package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.domain.model.ShoppingList

data class ShoppingListState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val errorMessage: String? = null,
    val isListCreated: Boolean = false,
    val isIconAdded: Boolean = false,
    val addedName: String = "",
    val addedId: Long = 0L,
    val addedIcon: Int = 0
) {
    val isCreateClicked: Boolean
        get() = shoppingLists.isNotEmpty()
}