package ru.practicum.shoppinglist.ui.main.viewmodel

sealed interface ShoppingListIntent {
    data object AddShoppingList : ShoppingListIntent
    data class SetAddedName(val addedName: String) : ShoppingListIntent
    data class SetAddedId(val addedId: Long) : ShoppingListIntent
    data class SetAddedIcon(val addedIcon: Int) : ShoppingListIntent
    data object GetAllShoppingList : ShoppingListIntent
    data object Delete : ShoppingListIntent
    data object ClearErrors : ShoppingListIntent
    data object ClearNewListState : ShoppingListIntent
    data class UpdateListIcon(val id: Long, val iconResId: Int) : ShoppingListIntent

    data class UpdateSearchQuery(val query: String) : ShoppingListIntent
    data class SetSearchActive(val active: Boolean) : ShoppingListIntent
    data object ShowCreateDialog : ShoppingListIntent
    data object HideDialog : ShoppingListIntent
    data class UpdateDialogName(val name: String) : ShoppingListIntent
    data object ShowDeleteAllDialog : ShoppingListIntent
    data object HideDeleteAllDialog : ShoppingListIntent
}