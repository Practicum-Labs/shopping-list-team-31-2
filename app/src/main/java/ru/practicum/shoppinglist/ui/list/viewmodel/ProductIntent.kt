package ru.practicum.shoppinglist.ui.list.viewmodel

import ru.practicum.shoppinglist.domain.model.Product

sealed class ProductIntent {
    data object LoadProducts : ProductIntent()
    data object SaveNewProduct : ProductIntent()
    data class AddProduct(val product: Product) : ProductIntent()
    data class UpdateProduct(val product: Product) : ProductIntent()
    data class DeleteProduct(val productId: Long) : ProductIntent()
    data object ShowBottomSheet : ProductIntent()
    data object HideBottomSheet : ProductIntent()
    data class OnInputValueChanged(val fieldType: FieldType, val value: String) : ProductIntent()
}

data class NewProductData(
    val name: String,
    val quantity: String,
    val unit: String
)

enum class FieldType { NAME, QUANTITY, UNIT }

sealed class UiEffect {
    data class ShowError(val message: String) : UiEffect()
}