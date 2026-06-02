package ru.practicum.shoppinglist.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    @ColumnInfo(name = "is_purchased")
    val isPurchased: Boolean,
    @ColumnInfo(name = "list_id")
    val listId: Long,
    @ColumnInfo(name = "order_position")
    val orderPosition: Int
)