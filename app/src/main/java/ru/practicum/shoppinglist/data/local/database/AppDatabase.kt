package ru.practicum.shoppinglist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity

@Database(
    entities = [ShoppingListEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
}