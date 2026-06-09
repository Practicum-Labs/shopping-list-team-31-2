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
import ru.practicum.shoppinglist.R
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

    init {
        processIntent(ShoppingListIntent.GetAllShoppingList)
    }

    fun processIntent(intent: ShoppingListIntent) = when (intent) {
        is ShoppingListIntent.AddShoppingList -> handleAddItem()
        is ShoppingListIntent.GetAllShoppingList -> handleGetAllItems()
        is ShoppingListIntent.SetAddedName -> _uiState.update { it.copy(
            addedName = intent.addedName,
            errorMessage = null
        ) }
        is ShoppingListIntent.SetAddedId -> _uiState.update { it.copy(addedId = intent.addedId) }
        is ShoppingListIntent.SetAddedIcon -> _uiState.update { it.copy(addedIcon = intent.addedIcon) }
        is ShoppingListIntent.Delete -> handleDelete()
        is ShoppingListIntent.ClearErrors -> _uiState.update { it.copy(errorMessage = null) }
        is ShoppingListIntent.ClearNewListState -> _uiState.update { it.copy(addedId = 0L) }
        is ShoppingListIntent.UpdateListIcon -> handleUpdateIcon(intent.id, intent.iconResId)
        else -> {}
    }

    private fun handleAddItem() {
        val name = _uiState.value.addedName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = R.string.error_empty_text.toString()) }
            return
        }

        val isDuplicate = _uiState.value.shoppingLists.any { it.name.equals(name, ignoreCase = true) }
        if (isDuplicate) {
            _uiState.update { it.copy(errorMessage = R.string.error_duplicate_name.toString()) }
            return
        }

        val shoppingList = ShoppingList(
            id = 0,
            name = name,
            icon = _uiState.value.addedIcon
        )
        viewModelScope.launch {
            try {
                val newId = shoppingListInteractor.createShoppingList(shoppingList)
                _uiState.update { currentState ->
                    currentState.copy(
                        addedId = newId,
                        addedIcon = R.drawable.ic_set_basket,
                        addedName = ""
                    )
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
                    _uiState.update { it.copy(shoppingLists = items.reversed()) }
                }
        }
    }

    private fun handleDelete() {
        viewModelScope.launch {
            shoppingListInteractor.delete()
        }
    }
    private fun handleUpdateIcon(id: Long, iconResId: Int) {
        viewModelScope.launch {
            try {
                shoppingListInteractor.updateListIcon(id, iconResId)
                _uiState.update { currentState ->
                    currentState.copy(
                        shoppingLists = currentState.shoppingLists.map {
                            if (it.id == id) it.copy(icon = iconResId) else it
                        }
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
