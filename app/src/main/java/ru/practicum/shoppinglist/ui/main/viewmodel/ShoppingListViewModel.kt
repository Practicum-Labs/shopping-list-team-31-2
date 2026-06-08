package ru.practicum.shoppinglist.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.domain.model.ShoppingList
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListInteractor: ShoppingListInteractor,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingListState())
    val uiState: StateFlow<ShoppingListState> = _uiState.asStateFlow()

    fun processIntent(intent: ShoppingListIntent) = when (intent) {
        is ShoppingListIntent.AddShoppingList -> handleAddItem(intent.shoppingList)
        is ShoppingListIntent.DeleteAll -> intent
        is ShoppingListIntent.GetAllShoppingList -> handleGetAllItems()
        is ShoppingListIntent.SetAddedName -> _uiState.update { it.copy(addedName = it.addedName) }
        is ShoppingListIntent.SetAddedId -> _uiState.update { it.copy(addedId = it.addedId) }
        is ShoppingListIntent.SetAddedIcon -> _uiState.update { it.copy(addedName = it.addedName) }
    }

    private fun handleAddItem(shoppingList: ShoppingList) {
        viewModelScope.launch {
            try {
                shoppingListInteractor.createShoppingList(shoppingList)
                _uiState.update { currentState ->
                    val mutableItems = currentState.shoppingLists.toMutableList()
                    mutableItems.add(shoppingList)
                    currentState.copy(shoppingLists = mutableItems, addedId = mutableItems.last().id + 1)
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun handleGetAllItems() {
        viewModelScope.launch {
            shoppingListInteractor
                .getShoppingLists()
                .catch { e ->
                    _uiState.update {
                        it.copy(errorMessage = e.message)
                    }
                }
                .collect { items ->
                    _uiState.update { it.copy(shoppingLists = items) }
                }
        }
    }
}
