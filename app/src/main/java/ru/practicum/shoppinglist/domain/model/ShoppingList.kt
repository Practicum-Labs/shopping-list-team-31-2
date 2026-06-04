package ru.practicum.shoppinglist.domain.model

data class ShoppingList (
    val id: Long,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)