package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.domain.model.ShoppingList

data class ShoppingListState(
    val shoppingLists: List<ShoppingList> = emptyList(),
    val errorMessage: String? = null,
    val addedName: String = "",
    val addedId: Long = 0L,
    val addedIcon: Int = R.drawable.ic_list_alt,

    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val dialogState: DialogState = DialogState.Hidden,
    val deleteAllDialogVisible: Boolean = false,

) {

    val displayLists: List<ShoppingList>
        get() = if (isSearchActive && searchQuery.isNotBlank()) {
            shoppingLists.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            shoppingLists
        }

}

sealed class DialogState {
    object Hidden : DialogState()
    data class Create(val name: String = "") : DialogState()

}