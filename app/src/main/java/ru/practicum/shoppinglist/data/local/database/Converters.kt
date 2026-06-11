package ru.practicum.shoppinglist.data.local.database

import ru.practicum.shoppinglist.data.local.entities.ProductEntity
import ru.practicum.shoppinglist.domain.model.Product
import javax.inject.Inject

class Converters @Inject constructor() {
    fun map(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            name = product.name,
            quantity = stringT0Double(product.quantity),
            unit = product.unit,
            listId = product.listId,
            isPurchased = product.isPurchased,
            orderPosition = product.position
        )
    }

    fun map(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            quantity = entity.quantity.toString(),
            unit = entity.unit,
            listId = entity.listId,
            isPurchased = entity.isPurchased,
            position = entity.orderPosition
        )
    }

    fun mapEntityListToProducts(entityList: List<ProductEntity>): List<Product> {
        return entityList.map { entity -> map(entity) }
    }

    fun mapToList(productsList: List<Product>): List<ProductEntity> {
        return productsList.map { product -> map(product) }
    }

    private fun stringT0Double(value: String): Double {
        return value.trim().toDoubleOrNull() ?: 0.0
    }

}