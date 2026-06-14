package ru.practicum.shoppinglist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.practicum.shoppinglist.data.local.dao.ProductDao
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.local.dao.UsersDao
import ru.practicum.shoppinglist.data.local.entities.ProductEntity
import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity
import ru.practicum.shoppinglist.data.local.entities.UserEntity

@Database(
    entities = [ShoppingListEntity::class, ProductEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun productDao(): ProductDao

    abstract fun usersDao(): UsersDao
}