package ru.practicum.shoppinglist.ui.main.viewmodel

import ru.practicum.shoppinglist.core.mvi.MviIntent
import ru.practicum.shoppinglist.domain.model.ShoppingList

sealed interface ShoppingListIntent : MviIntent {

    data object createClicked : ShoppingListIntent
    data object deleteAll : ShoppingListIntent
}