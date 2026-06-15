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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.domain.model.Product
import ru.practicum.shoppinglist.domain.repository.ProductInteractor
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught", "SwallowedException", "LongParameterList")
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
            val data = contentState.newProductData
            var currentData = data
            if (currentData == null || (currentData.name.isBlank() && currentData.quantity.isBlank() && currentData.unit.isBlank())) {
                val savedName = savedStateHandle.get<String>(KEY_SAVED_NAME) ?: ""
                val savedQuantity = savedStateHandle.get<String>(KEY_SAVED_QUANTITY) ?: ""
                val savedUnit = savedStateHandle.get<String>(KEY_SAVED_UNIT) ?: ""
                currentData = NewProductData(savedName, savedQuantity, savedUnit)
            }

            if (currentData.name.isBlank()) {
                viewModelScope.launch {
                    _uiEffect.emit(UiEffect.ShowError("Введите название продукта"))
                }
                return
            }

            val quantity = if (currentData.quantity.isBlank()) "1" else currentData.quantity
            val unit = if (currentData.unit.isBlank()) "шт" else currentData.unit

            val newProduct = Product(
                id = 0,
                name = currentData.name,
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

                    _state.update { currentState ->
                        val current =
                            currentState as? ProductsState.Content ?: return@update currentState
                        current.copy(
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

    @Suppress("LabeledExpression")
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                productInteractor.getProductsInShoppingList(shoppingListId).collect { products ->

                    _state.update { currentState ->
                        if (currentState is ProductsState.Loading) {
                            ProductsState.Content(
                                products = products,
                                originalProducts = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        } else {
                            val current =
                                currentState as? ProductsState.Content ?: ProductsState.Content()
                            current.copy(
                                products = products,
                                originalProducts = products,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Ошибка загрузки: ${e.message}"))
            }
        }
    }

    private fun sortProductsAlphabetically() {
        _state.update { currentState ->
            val current = currentState as? ProductsState.Content ?: return@update currentState
            val sortedProducts = current.products.sortedBy { it.name.lowercase() }
            current.copy(
                products = sortedProducts,
                isSortMenuVisible = false
            )
        }
        viewModelScope.launch {
            _uiEffect.emit(UiEffect.ShowMessage("Список отсортирован по алфавиту"))
        }
    }

    private fun deleteAllProducts() {
        viewModelScope.launch {
            _state.update { currentState ->
                val current = currentState as? ProductsState.Content ?: return@update currentState
                current.copy(isLoading = true)
            }

            try {
                productInteractor.deleteProductByShoppingList(shoppingListId)
                loadProducts()
                _uiEffect.emit(UiEffect.ShowMessage("Все продукты удалены"))
                _state.update { currentState ->
                    val current =
                        currentState as? ProductsState.Content ?: return@update currentState
                    current.copy(isSortMenuVisible = false)
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить продукты"))
                _state.update { currentState ->
                    val current =
                        currentState as? ProductsState.Content ?: return@update currentState
                    current.copy(isLoading = false)
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

            _state.update { currentState ->
                val current = currentState as? ProductsState.Content ?: return@update currentState
                current.copy(isLoading = true)
            }

            try {
                purchasedProducts.forEach { product ->
                    productInteractor.deleteProduct(product.id)
                }
                loadProducts()
                _uiEffect.emit(UiEffect.ShowMessage("Купленные продукты удалены"))
                _state.update { currentState ->
                    val current =
                        currentState as? ProductsState.Content ?: return@update currentState
                    current.copy(isSortMenuVisible = false)
                }
            } catch (e: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить купленные продукты"))
                _state.update { currentState ->
                    val current =
                        currentState as? ProductsState.Content ?: return@update currentState
                    current.copy(isLoading = false)
                }
            }
        }
    }

    private fun showSortMenu() {
        _state.update { currentState ->
            val current = currentState as? ProductsState.Content ?: return@update currentState
            current.copy(isSortMenuVisible = true)
        }
    }

    private fun hideSortMenu() {
        _state.update { currentState ->
            val current = currentState as? ProductsState.Content ?: return@update currentState
            current.copy(isSortMenuVisible = false)
        }
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
