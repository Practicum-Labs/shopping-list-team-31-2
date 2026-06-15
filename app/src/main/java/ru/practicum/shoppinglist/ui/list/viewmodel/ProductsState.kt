package ru.practicum.shoppinglist.ui.list.viewmodel

import androidx.compose.runtime.Immutable
import ru.practicum.shoppinglist.domain.model.Product

@Immutable
sealed interface ProductsState {
    data object Loading : ProductsState
    @Immutable
    data class Content(
        val products: List<Product> = emptyList(),
        val originalProducts: List<Product> = emptyList(),
        val newProductData: NewProductData? = null,
        val isBottomSheetVisible: Boolean = false,
        val isSortMenuVisible: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isFirstTimeOpening: Boolean = true
    ) : ProductsState
}