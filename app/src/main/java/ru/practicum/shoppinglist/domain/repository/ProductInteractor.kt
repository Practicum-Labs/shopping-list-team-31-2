package ru.practicum.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.Product

interface ProductInteractor {
    suspend fun addProduct(product: Product): Long
    suspend fun getProductById(id: Long): Product?
    suspend fun getProductsInShoppingList(idShoppingList: Long): Flow<List<Product>>
    suspend fun deleteProduct(id: Long)
    suspend fun deleteProductByShoppingList(idShoppingList: Long)
    suspend fun updateProduct(product: Product)
}