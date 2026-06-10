package ru.practicum.shoppinglist.ui.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.domain.model.Product
import ru.practicum.shoppinglist.domain.repository.ProductRepository
import javax.inject.Inject

class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProductsState>(ProductsState.Empty)
    val state = _state.asStateFlow()

    private val _uiEffect = MutableSharedFlow<UiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val intents = MutableSharedFlow<ProductIntent>()

    private var shoppingListId: Long = 1L

    init {
        viewModelScope.launch { intents.emit(ProductIntent.LoadProducts) }
        handleIntents()
    }

    // Функции отвечающие за работу Intent c View (UI)

    fun sendIntent(intent: ProductIntent) {
        viewModelScope.launch { intents.emit(intent) }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intents.collect { intent ->
                when (intent) {
                    is ProductIntent.LoadProducts -> getProducts()
                    is ProductIntent.AddProduct -> addProduct(intent.product)
                    is ProductIntent.UpdateProduct -> updateProduct(intent.product)
                    is ProductIntent.DeleteProduct -> deleteProduct(intent.productId)
                    is ProductIntent.ShowAddProductBottomSheet -> showBottomSheet()
                    is ProductIntent.HideBottomSheet -> hideBottomSheet()
                    is ProductIntent.OnInputValueChanged -> updateNewProductField(intent.fieldType, intent.value)
                    is ProductIntent.SaveNewProduct -> saveNewProduct()
                }
            }
        }
    }

    // ---ОБРАБОТЧИКИ INTENT---
    private fun getProducts() {
        viewModelScope.launch {
            productRepository.getProductsInShoppingList(shoppingListId).collect { products ->
                _state.value = if (products.isNotEmpty()) {
                    ProductsState.Content(products = products)
                } else {
                    ProductsState.Empty
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
                productRepository.updateProduct(product)
            } catch (_: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось обновить продукт"))
            }
        }
    }

    private fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(productId)
            } catch (_: Exception) {
                _uiEffect.emit(UiEffect.ShowError("Не удалось удалить продукт"))
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
            _state.value = contentState.copy(newProductData = updatedData)
        }
    }

    // Управление BottomSheet

    private fun showBottomSheet() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            // При открытии BottomSheet устанавливает пустые данные, если их нет
            val data = contentState.newProductData ?: NewProductData("", "", "")
            _state.value = contentState.copy(
                newProductData = data,
                isBottomSheetVisible = true
            )
        }
    }

    private fun hideBottomSheet() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            _state.value = contentState.copy(
                newProductData = null,
                isBottomSheetVisible = false
            )
        }
    }

    private fun saveNewProduct() {
        (_state.value as? ProductsState.Content)?.let { contentState ->
            val data = contentState.newProductData ?: NewProductData("", "", "")

            // Здесь можно добавить проверку данных перед сохранением

            if (data.name.isNotBlank() && data.quantity.isNotBlank()) {
                val newProduct = Product(
                    id = 0,
                    name = data.name,
                    quantity = data.quantity,
                    unit = data.unit,
                    isPurchased = false,
                    listId = shoppingListId,
                    position = contentState.products.size
                )

                viewModelScope.launch {
                    try {
                        productRepository.addProduct(newProduct)
                        hideBottomSheet()
                    } catch (_: Exception) {
                        // Можно показать сообщение об ошибке
                        _uiEffect.emit(UiEffect.ShowError("Не удалось сохранить продукт"))
                    }
                }

            } else {
                viewModelScope.launch {
                    _uiEffect.emit(UiEffect.ShowError("Заполните все поля"))
                }
            }
        }
    }

  /*
  // Для теста
    private fun getProductsTest() {
        val products = getLists()
        _state.value = ProductsState.Content(products)
    }

    private fun getLists(): List<Product> {
        return listOf(
            Product(
                id = 0,
                name = "Яблоко",
                quantity = "1",
                unit = "кг",
                isPurchased = false,
                listId = 0,
                position = 0,
                isChecked = true
            ),
            Product(
                id = 1,
                name = "Яйца",
                quantity = "10",
                unit = "шт",
                isPurchased = false,
                listId = 0,
                position = 1,
                isChecked = false
            ),
            Product(
                id = 2,
                name = "Молоко",
                quantity = "1",
                unit = "л",
                isPurchased = false,
                listId = 0,
                position = 2,
                isChecked = false
            ),
            Product(
                id = 3,
                name = "Сыр",
                quantity = "1",
                unit = "уп",
                isPurchased = false,
                listId = 0,
                position = 3,
                isChecked = false
            ),
        )
    }

   */
}
