package ru.practicum.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.shoppinglist.data.local.dao.ProductDao
import ru.practicum.shoppinglist.data.local.database.Converters
import ru.practicum.shoppinglist.domain.model.Product
import ru.practicum.shoppinglist.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao,
    private val converter: Converters
) : ProductRepository {

    override suspend fun addProduct(product: Product): Long {
        return dao.insertProduct(converter.map(product))
    }

    override suspend fun getProductById(id: Long): Product? {
        return dao.getProductById(id)?.let { converter.map(it) }
    }

    override suspend fun getProductsInShoppingList(idShoppingList: Long): Flow<List<Product>> {
        return dao.getProductsByListId(idShoppingList).map { entityList ->
            converter.mapEntityListToProducts(entityList)
        }
    }

    override suspend fun deleteProduct(id: Long) {
        dao.deleteProductById(id)
    }

    override suspend fun deleteProductByShoppingList(idShoppingList: Long) {
        dao.deleteProductsByListId(idShoppingList)
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(converter.map(product))
    }

}