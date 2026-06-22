package ru.practicum.shoppinglist.ui.main.viewmodel

import android.util.Log
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

    private val userId: Long = savedStateHandle.get<Long>("userId") ?: -1L
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
            is ShoppingListIntent.GetAllShoppingList -> handleGetAllItemsByUserId()

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

            is ShoppingListIntent.ShowRenameDialog -> {
                _uiState.update {
                    it.copy(dialogState = DialogState.Rename(intent.listId, intent.currentName))
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
                is DialogState.Rename -> current.copy(currentName = intent.name)
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
            icon = _uiState.value.addedIcon,
            userId = userId
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

    private fun handleGetAllItemsByUserId() {
        viewModelScope.launch {
            shoppingListInteractor.getShoppingListsByUserId(userId = userId).catch { e ->
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
                shoppingListInteractor.deleteAllListsByUserId(userId)
                _uiState.update {
                    it.copy(
                        deleteAllDialogVisible = false,
                        searchQuery = "",
                        isSearchActive = false
                    )
                }
                handleGetAllItemsByUserId()
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
                handleGetAllItemsByUserId()
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_rename))
                _uiState.update { it.copy(isLoading = false) }
                Log.e(LOG_TAG, LOG_MESSAGE + e)
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
                handleGetAllItemsByUserId()
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_delete))
                _uiState.update { it.copy(isLoading = false) }
                Log.e(LOG_TAG, LOG_MESSAGE + e)

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
                    icon = originalList?.icon ?: R.drawable.ic_list_alt,
                    userId = userId
                )
                shoppingListInteractor.createShoppingList(newList)
                _effect.emit(ShoppingListEffect.ShowMessage(R.string.list_copied))
                _uiState.update { it.copy(isLoading = false) }
                handleGetAllItemsByUserId()
            } catch (e: IOException) {
                _effect.emit(ShoppingListEffect.ShowError(R.string.error_copy))
                _uiState.update { it.copy(isLoading = false) }
                Log.e(LOG_TAG, LOG_MESSAGE + e)

            }
        }
    }
    companion object {
        const val LOG_TAG = "ShoppingListViewModel"
        const val LOG_MESSAGE = "Error deleting list:"
    }
}
