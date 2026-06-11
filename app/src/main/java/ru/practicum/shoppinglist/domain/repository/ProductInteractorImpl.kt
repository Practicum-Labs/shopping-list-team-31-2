package ru.practicum.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.domain.model.Product

class ProductInteractorImpl(
    val productRepository: ProductRepository
) : ProductInteractor {
    override suspend fun addProduct(product: Product): Long {
        return productRepository.addProduct(product)
    }

    override suspend fun getProductById(id: Long): Product? {
        return productRepository.getProductById(id)
    }

    override suspend fun getProductsInShoppingList(idShoppingList: Long): Flow<List<Product>> {
        return productRepository.getProductsInShoppingList(idShoppingList)
    }

    override suspend fun deleteProduct(id: Long) {
        productRepository.deleteProduct(id)
    }

    override suspend fun deleteProductByShoppingList(idShoppingList: Long) {
        productRepository.deleteProductByShoppingList(idShoppingList)
    }

    override suspend fun updateProduct(product: Product) {
        productRepository.updateProduct(product)
    }
}