package ru.practicum.shoppinglist.ui.list.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.domain.model.Product
import ru.practicum.shoppinglist.domain.repository.ProductInteractor
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productInteractor: ProductInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<ProductsState>(ProductsState.Loading)
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val intents = MutableSharedFlow<ProductIntent>()

    private var shoppingListId: Long = 1L

    init {
        viewModelScope.launch { intents.emit(ProductIntent.LoadProducts) }
        handleIntents()
    }

    fun sendIntent(intent: ProductIntent) {
        viewModelScope.launch { intents.emit(intent) }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intents.collect { intent ->
                when (intent) {
                    is ProductIntent.LoadProducts -> loadProducts()
                    is ProductIntent.AddProduct -> addProduct(intent.product)
                    is ProductIntent.UpdateProduct -> updateProduct(intent.product)
                    is ProductIntent.DeleteProduct -> deleteProduct(intent.productId)
                    is ProductIntent.ShowBottomSheet -> showBottomSheet()
                    is ProductIntent.HideBottomSheet -> hideBottomSheet()
                    is ProductIntent.OnInputValueChanged -> updateNewProductField(
                        intent.fieldType,
                        intent.value
                    )

                    is ProductIntent.SaveNewProduct -> saveNewProduct()
                    is ProductIntent.SortProductsAlphabetically -> sortProductsAlphabetically()
                    is ProductIntent.DeleteAllProducts -> deleteAllProducts()
                    is ProductIntent.DeletePurchasedProducts -> deletePurchasedProducts()
                    is ProductIntent.ShowSortMenu -> showSortMenu()
                    is ProductIntent.HideSortMenu -> hideSortMenu()
                    is ProductIntent.ToggleProductPurchased -> toggleProductPurchased(
                        intent.productId,
                        intent.isPurchased
                    )

                }
            }
        }
    }

    private fun addProduct(product: Product) {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            val updatedList = contentState.products + product
            _state.value = contentState.copy(products = updatedList)
        }
    }

    private fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                productInteractor.updateProduct(product)

            } catch (_: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось обновить продукт"))
            }
        }
    }

    private fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                productInteractor.deleteProduct(productId)
            } catch (_: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить продукт"))
            }
        }
    }

    private fun showBottomSheet() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            val shouldCreateNewData = contentState.isFirstTimeOpening ||
                contentState.newProductData == null ||
                contentState.newProductData?.name?.isBlank() == true &&
                contentState.newProductData?.quantity?.isBlank() == true &&
                contentState.newProductData?.unit?.isBlank() == true

            val initialData = if (shouldCreateNewData) {
                NewProductData("", "", "")
            } else {
                contentState.newProductData ?: NewProductData("", "", "")
            }

            _state.value = contentState.copy(
                newProductData = initialData,
                isBottomSheetVisible = true,
                isFirstTimeOpening = false
            )
        }
    }

    private fun hideBottomSheet() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            _state.value = contentState.copy(
                isBottomSheetVisible = false
            )
        }
    }

    private fun saveNewProduct() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            val currentData = getCurrentProductData(contentState) ?: return
            if (currentData.name.isBlank()) {
                showError("Введите название продукта")
                return
            }
            executeSaveProduct(currentData, contentState)
        }
    }

    private fun getCurrentProductData(contentState: ProductsState.Content): NewProductData? {
        var currentData = contentState.newProductData
        if (currentData == null || currentData.name.isBlank() && currentData.quantity.isBlank() && currentData.unit.isBlank()) {
            val savedName = savedStateHandle.get<String>(KEY_SAVED_NAME) ?: ""
            val savedQuantity = savedStateHandle.get<String>(KEY_SAVED_QUANTITY) ?: ""
            val savedUnit = savedStateHandle.get<String>(KEY_SAVED_UNIT) ?: ""
            currentData = NewProductData(savedName, savedQuantity, savedUnit)
        }
        return currentData
    }

    private fun executeSaveProduct(data: NewProductData, contentState: ProductsState.Content) {
        val quantity = if (data.quantity.isBlank()) "1" else data.quantity
        val unit = if (data.unit.isBlank()) "шт" else data.unit

        val newProduct = Product(
            id = 0,
            name = data.name,
            quantity = quantity,
            unit = unit,
            isPurchased = false,
            listId = shoppingListId,
            position = contentState.products.size
        )

        viewModelScope.launch {
            try {
                productInteractor.addProduct(newProduct)
                savedStateHandle.remove<String>(KEY_SAVED_NAME)
                savedStateHandle.remove<String>(KEY_SAVED_QUANTITY)
                savedStateHandle.remove<String>(KEY_SAVED_UNIT)

                val currentState = _state.value as? ProductsState.Content
                if (currentState != null) {
                    _state.value = currentState.copy(
                        newProductData = NewProductData("", "", ""),
                        isBottomSheetVisible = false,
                        isFirstTimeOpening = true
                    )
                }
                loadProducts()
                _uiEffect.emit(UiEffect.ShowMessage("Продукт добавлен"))
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось сохранить продукт: ${e.message}"))
            }
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch { _uiEffect.emit(UiEffect.ShowError(message)) }
    }

    private fun updateNewProductField(fieldType: FieldType, value: String) {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            val data = contentState.newProductData ?: NewProductData("", "", "")

            val updatedData = when (fieldType) {
                FieldType.NAME -> data.copy(name = value)
                FieldType.QUANTITY -> data.copy(quantity = value)
                FieldType.UNIT -> data.copy(unit = value)
            }

            savedStateHandle[KEY_SAVED_NAME] = updatedData.name
            savedStateHandle[KEY_SAVED_QUANTITY] = updatedData.quantity
            savedStateHandle[KEY_SAVED_UNIT] = updatedData.unit

            _state.value = contentState.copy(newProductData = updatedData)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                productInteractor.getProductsInShoppingList(shoppingListId).collect { products ->
                    val newState = when (val currentState = _state.value) {
                        is ProductsState.Loading -> {
                            ProductsState.Content(
                                products = products,
                                originalProducts = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is ProductsState.Content -> {
                            currentState.copy(
                                products = products,
                                originalProducts = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        else -> {
                            ProductsState.Content(
                                products = products,
                                originalProducts = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    _state.value = newState
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Ошибка загрузки: ${e.message}"))
            }
        }
    }

    private fun sortProductsAlphabetically() {
        val currentState = _state.value as? ProductsState.Content ?: return
        val sortedProducts = currentState.products.sortedBy { it.name.lowercase() }
        _state.value = currentState.copy(
            products = sortedProducts,
            originalProducts = sortedProducts,
            isSortMenuVisible = false
        )
        viewModelScope.launch {
            _uiEffect.emit(UiEffect.ShowMessage("Список отсортирован по алфавиту"))
        }
    }

    private fun deleteAllProducts() {
        viewModelScope.launch {
            val currentState = _state.value as? ProductsState.Content
            if (currentState == null) return@launch

            _state.value = currentState.copy(isLoading = true)

            try {
                productInteractor.deleteProductByShoppingList(shoppingListId)
                loadProducts()
                _uiEffect.emit(UiEffect.ShowMessage("Все продукты удалены"))

                val updatedState = _state.value as? ProductsState.Content
                if (updatedState != null) {
                    _state.value = updatedState.copy(isSortMenuVisible = false)
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить продукты"))
                val errorState = _state.value as? ProductsState.Content
                if (errorState != null) {
                    _state.value = errorState.copy(isLoading = false)
                }
            }
        }
    }

    private fun deletePurchasedProducts() {
        viewModelScope.launch {
            val currentState = _state.value as? ProductsState.Content ?: return@launch
            val purchasedProducts = currentState.products.filter { it.isPurchased }

            if (purchasedProducts.isEmpty()) {
                _uiEffect.emit(UiEffect.ShowMessage("Нет купленных продуктов"))
                return@launch
            }

            _state.value = currentState.copy(isLoading = true)

            try {
                purchasedProducts.forEach { product ->
                    productInteractor.deleteProduct(product.id)
                }
                loadProducts()
                _uiEffect.emit(UiEffect.ShowMessage("Купленные продукты удалены"))

                val updatedState = _state.value as? ProductsState.Content
                if (updatedState != null) {
                    _state.value = updatedState.copy(isSortMenuVisible = false)
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить купленные продукты"))
                val errorState = _state.value as? ProductsState.Content
                if (errorState != null) {
                    _state.value = errorState.copy(isLoading = false)
                }
            }
        }
    }

    private fun showSortMenu() {
        val currentState = _state.value as? ProductsState.Content ?: return
        _state.value = currentState.copy(isSortMenuVisible = true)
    }

    private fun hideSortMenu() {
        val currentState = _state.value as? ProductsState.Content ?: return
        _state.value = currentState.copy(isSortMenuVisible = false)
    }

    fun setShoppingListId(listId: Long) {
        shoppingListId = listId
    }

    private fun toggleProductPurchased(productId: Long, isPurchased: Boolean) {
        viewModelScope.launch {
            try {
                val currentState = _state.value as? ProductsState.Content
                val product = currentState?.products?.find { it.id == productId }

                if (product != null) {
                    val updatedProduct = product.copy(
                        isPurchased = !isPurchased
                    )
                    productInteractor.updateProduct(updatedProduct)
                    loadProducts()

                    val statusMessage =
                        if (!isPurchased) "Товар отмечен как купленный" else "Товар отмечен как не купленный"
                    _uiEffect.emit(UiEffect.ShowMessage(statusMessage))
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось обновить статус товара"))
            }
        }
    }

    companion object {
        private const val KEY_SAVED_NAME = "saved_product_name"
        private const val KEY_SAVED_QUANTITY = "saved_product_quantity"
        private const val KEY_SAVED_UNIT = "saved_product_unit"
    }

}
