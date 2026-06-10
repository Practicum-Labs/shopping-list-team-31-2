package ru.practicum.shoppinglist.ui.list.viewmodel

import androidx.compose.runtime.Immutable
import ru.practicum.shoppinglist.domain.model.Product

@Immutable
sealed interface ProductsState {
    @Immutable
    data class Content(
        val products: List<Product>,
        val newProductData: NewProductData? = null,
        val isBottomSheetVisible: Boolean = false
    ) : ProductsState

    @Immutable
    data object Empty : ProductsState
}