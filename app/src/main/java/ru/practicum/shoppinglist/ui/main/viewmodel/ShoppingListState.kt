package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.domain.model.ShoppingList

data class ShoppingListState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val errorMessage: String? = null,
    val addedName: String = "",
    val addedId: Long = 0L,
    val addedIcon: Int = R.drawable.ic_set_basket

)