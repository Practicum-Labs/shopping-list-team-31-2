package ru.practicum.shoppinglist.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingListState())
    val uiState: StateFlow<ShoppingListState> = _uiState.asStateFlow()

    init {
        processIntent(ShoppingListIntent.GetAllShoppingList)
    }

    fun processIntent(intent: ShoppingListIntent) {
        when (intent) {
            is ShoppingListIntent.AddShoppingList -> handleAddItem()
            is ShoppingListIntent.GetAllShoppingList -> handleGetAllItems()

            is ShoppingListIntent.SetAddedName -> updateAddedName(intent)
            is ShoppingListIntent.SetAddedId -> updateAddedId(intent)
            is ShoppingListIntent.SetAddedIcon -> updateAddedIcon(intent)

            is ShoppingListIntent.Delete -> handleDeleteAllLists()
            is ShoppingListIntent.ClearErrors -> clearErrors()
            is ShoppingListIntent.ClearNewListState -> clearNewListState()

            is ShoppingListIntent.UpdateListIcon -> handleUpdateIcon(intent.id, intent.iconResId)

            is ShoppingListIntent.UpdateSearchQuery -> updateSearchQuery(intent)

            is ShoppingListIntent.SetSearchActive -> updateSearchState(intent)

            is ShoppingListIntent.ShowCreateDialog -> showCreateDialog()

            is ShoppingListIntent.HideDialog -> hideDialog()

            is ShoppingListIntent.UpdateDialogName -> updateDialogName(intent)

            is ShoppingListIntent.ShowDeleteAllDialog -> showDeleteDialog()

            is ShoppingListIntent.HideDeleteAllDialog -> hideDeleteDialog()
        }
    }

    private fun updateSearchState(
        intent: ShoppingListIntent.SetSearchActive
    ) {
        _uiState.update {
            if (intent.active) {
                it.copy(isSearchActive = true)
            } else {
                it.copy(
                    isSearchActive = false,
                    searchQuery = ""
                )
            }
        }
        savedStateHandle["is_search_active"] = intent.active
    }

    private fun showCreateDialog() {
        _uiState.update {
            it.copy(dialogState = DialogState.Create(""))
        }
        savedStateHandle["dialog_type"] = "create"
    }

    private fun hideDialog() {
        _uiState.update {
            it.copy(dialogState = DialogState.Hidden)
        }
        savedStateHandle["dialog_type"] = null
    }

    private fun updateDialogName(
        intent: ShoppingListIntent.UpdateDialogName
    ) {
        _uiState.update { state ->

            val dialog = when (val current = state.dialogState) {
                is DialogState.Create -> current.copy(name = intent.name)

                else -> current
            }

            state.copy(dialogState = dialog)
        }

        savedStateHandle["dialog_name"] = intent.name
    }

    private fun showDeleteDialog() {
        _uiState.update {
            it.copy(deleteAllDialogVisible = true)
        }
    }

    private fun hideDeleteDialog() {
        _uiState.update {
            it.copy(deleteAllDialogVisible = false)
        }
    }

    private fun updateSearchQuery(intent: ShoppingListIntent.UpdateSearchQuery) {
        _uiState.update {
            it.copy(searchQuery = intent.query)
        }

        savedStateHandle["search_query"] = intent.query
    }

    private fun updateAddedName(intent: ShoppingListIntent.SetAddedName) {
        _uiState.update {
            it.copy(
                addedName = intent.addedName,
                errorMessage = null
            )
        }
    }

    private fun updateAddedId(intent: ShoppingListIntent.SetAddedId) {
        _uiState.update {
            it.copy(addedId = intent.addedId)
        }
    }

    private fun updateAddedIcon(intent: ShoppingListIntent.SetAddedIcon) {
        _uiState.update {
            it.copy(addedIcon = intent.addedIcon)
        }
    }

    private fun clearErrors() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun clearNewListState() {
        _uiState.update {
            it.copy(addedId = 0L)
        }
    }

    private fun handleAddItem() {
        val name = _uiState.value.addedName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = R.string.error_empty_text.toString()) }
            return
        }

        val isDuplicate =
            _uiState.value.shoppingLists.any { it.name.equals(name, ignoreCase = true) }
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
                        addedIcon = R.drawable.ic_list_alt,
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
            shoppingListInteractor.getShoppingLists().catch { e ->
                _uiState.update {
                    it.copy(errorMessage = e.message)
                }
            }.collect { items ->
                _uiState.update { it.copy(shoppingLists = items) }
            }
        }
    }

    private fun handleDeleteAllLists() {
        viewModelScope.launch {
            try {
                shoppingListInteractor.delete()
                _uiState.update {
                    it.copy(
                        deleteAllDialogVisible = false,
                        searchQuery = "",
                        isSearchActive = false
                    )
                }
                // Перезагружаем списки
                handleGetAllItems()
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
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
