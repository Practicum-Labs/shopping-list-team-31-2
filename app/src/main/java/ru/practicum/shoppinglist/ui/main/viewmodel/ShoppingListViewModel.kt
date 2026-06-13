package ru.practicum.shoppinglist.ui.main.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _effect = MutableSharedFlow<ShoppingListEffect>()
    val effect: SharedFlow<ShoppingListEffect> = _effect.asSharedFlow()

    init {
        processIntent(ShoppingListIntent.GetAllShoppingList)
    }

    fun processIntent(intent: ShoppingListIntent) {
        when (intent) {
            is ShoppingListIntent.AddShoppingList -> handleAddItem()
            is ShoppingListIntent.GetAllShoppingList -> handleGetAllItems()
            is ShoppingListIntent.SetAddedName -> _uiState.update {
                it.copy(
                    addedName = intent.addedName,
                    errorMessage = null
                )
            }

            is ShoppingListIntent.SetAddedId -> _uiState.update { it.copy(addedId = intent.addedId) }
            is ShoppingListIntent.SetAddedIcon -> _uiState.update { it.copy(addedIcon = intent.addedIcon) }
            is ShoppingListIntent.Delete -> handleDeleteAllLists()
            is ShoppingListIntent.ClearErrors -> _uiState.update { it.copy(errorMessage = null) }
            is ShoppingListIntent.ClearNewListState -> _uiState.update { it.copy(addedId = 0L) }
            is ShoppingListIntent.UpdateListIcon -> handleUpdateIcon(intent.id, intent.iconResId)

            is ShoppingListIntent.UpdateSearchQuery -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
                savedStateHandle["search_query"] = intent.query
            }

            is ShoppingListIntent.SetSearchActive -> {
                _uiState.update {
                    if (!intent.active) {
                        it.copy(isSearchActive = false, searchQuery = "")
                    } else {
                        it.copy(isSearchActive = true)
                    }
                }
                savedStateHandle["is_search_active"] = intent.active
            }

            is ShoppingListIntent.ShowCreateDialog -> {
                _uiState.update { it.copy(dialogState = DialogState.Create(name = "")) }
                savedStateHandle["dialog_type"] = "create"
            }

            is ShoppingListIntent.HideDialog -> {
                _uiState.update { it.copy(dialogState = DialogState.Hidden) }
                savedStateHandle.remove("dialog_type")
            }

            is ShoppingListIntent.UpdateDialogName -> {
                _uiState.update { currentState ->
                    val newDialogState = when (val dialog = currentState.dialogState) {
                        is DialogState.Create -> dialog.copy(name = intent.name)
                        is DialogState.Rename -> dialog.copy(currentName = intent.name)
                        else -> dialog
                    }
                    currentState.copy(dialogState = newDialogState)
                }
                savedStateHandle["dialog_name"] = intent.name
            }

            is ShoppingListIntent.ShowDeleteAllDialog -> {
                _uiState.update { it.copy(deleteAllDialogVisible = true) }
            }

            is ShoppingListIntent.HideDeleteAllDialog -> {
                _uiState.update { it.copy(deleteAllDialogVisible = false) }
            }

            is ShoppingListIntent.ShowRenameDialog -> {
                _uiState.update {
                    it.copy(dialogState = DialogState.Rename(
                        listId = intent.listId,
                        currentName = intent.currentName
                    ))
                }
            }
            is ShoppingListIntent.RenameList -> handleRenameList(intent.listId, intent.newName)
            is ShoppingListIntent.ShowDeleteListDialog -> {
                _uiState.update {
                    it.copy(
                        deleteDialogVisible = true,
                        listToDeleteId = intent.listId,
                        listToDeleteName = intent.listName
                    )
                }
            }
            is ShoppingListIntent.DeleteList -> handleDeleteList(intent.listId)

            is ShoppingListIntent.CopyList -> handleCopyList(intent.listId, intent.listName)

            is ShoppingListIntent.HideDeleteListDialog -> {
                _uiState.update { it.copy(deleteDialogVisible = false) }
            }

            else -> {}
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

    private fun handleRenameList(listId: Long, newName: String) {
        if (newName.isBlank()) {
            viewModelScope.launch {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_empty_text))
            }
            return
        }

        val isDuplicate = _uiState.value.shoppingLists.any {
            it.id != listId && it.name.equals(newName, ignoreCase = true)
        }
        if (isDuplicate) {
            viewModelScope.launch {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_duplicate_name))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                shoppingListInteractor.renameList(listId, newName)
                _effect.emit(ShoppingListEffect.ShowMessageWithArgs(R.string.list_renamed, arrayOf(newName)))
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dialogState = DialogState.Hidden,
                        renameDialogName = ""
                    )
                }
                handleGetAllItems() // обновляем список
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_rename))
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleDeleteList(listId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, deleteDialogVisible = false) }
            try {
                shoppingListInteractor.deleteListById(listId)
                _effect.emit(ShoppingListEffect.ShowMessage(R.string.list_deleted))
                _uiState.update { it.copy(isLoading = false) }
                handleGetAllItems()
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_delete))
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleCopyList(listId: Long, originalName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val newName = "$originalName (Копия)"

                val originalList = _uiState.value.shoppingLists.find { it.id == listId }

                val newList = ShoppingList(
                    id = 0,
                    name = newName,
                    icon = originalList?.icon ?: R.drawable.ic_list_alt
                )
                val newId = shoppingListInteractor.createShoppingList(newList)
                _effect.emit(ShoppingListEffect.ShowMessage(R.string.list_copied))
                _uiState.update { it.copy(isLoading = false) }
                handleGetAllItems()
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_copy))
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
