package ru.practicum.shoppinglist.domain.model

data class Product(
    val id: Long,
    val name: String,
    val quantity: String,
    val unit: String,
    val isPurchased: Boolean,
    val listId: Long,
    val position: Int,
    val isChecked: Boolean = false,
)