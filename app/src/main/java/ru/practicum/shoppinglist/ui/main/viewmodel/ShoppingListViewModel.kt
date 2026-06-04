package ru.practicum.shoppinglist.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.practicum.shoppinglist.core.mvi.MviViewModel
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListInteractor: ShoppingListInteractor,
    savedStateHandle: SavedStateHandle
) : MviViewModel<ShoppingListIntent, ShoppingListState, ShoppingListEffect>(initialState = ShoppingListState) {
    override fun reduce(
        intent: ShoppingListIntent,
        current: ShoppingListState
    ): ShoppingListState {
        TODO("Not yet implemented")
    }
}