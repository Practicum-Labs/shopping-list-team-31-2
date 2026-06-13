package ru.practicum.shoppinglist.ui.main.viewmodel

sealed class ShoppingListEffect {
    data class ShowMessage(val messageResId: Int) : ShoppingListEffect()
    data class ShowMessageWithArgs(val messageResId: Int, val args: Array<Any>) : ShoppingListEffect()
    data class ShowError(val messageResId: Int) : ShoppingListEffect()
    data class ShowErrorText(val message: String) : ShoppingListEffect()

}