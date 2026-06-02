package ru.practicum.shoppinglist.data.repository

import ru.practicum.shoppinglist.data.local.dao.ProductDao
import ru.practicum.shoppinglist.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository