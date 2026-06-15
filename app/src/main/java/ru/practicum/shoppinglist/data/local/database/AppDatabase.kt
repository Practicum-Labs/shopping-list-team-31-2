package ru.practicum.shoppinglist.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.practicum.shoppinglist.data.local.dao.ProductDao
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.local.dao.UsersDao
import ru.practicum.shoppinglist.data.local.entities.ProductEntity
import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity
import ru.practicum.shoppinglist.data.local.entities.UserEntity

@Database(
    entities = [ShoppingListEntity::class, ProductEntity::class, UserEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun productDao(): ProductDao
    abstract fun usersDao(): UsersDao
    companion object {
        private const val DATABASE_NAME = "shopping_list.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}