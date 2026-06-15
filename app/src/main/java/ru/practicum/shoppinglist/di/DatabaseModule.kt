package ru.practicum.shoppinglist.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.practicum.shoppinglist.data.local.dao.ProductDao
import ru.practicum.shoppinglist.data.local.dao.ShoppingListDao
import ru.practicum.shoppinglist.data.local.dao.UsersDao
import ru.practicum.shoppinglist.data.local.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shopping_list_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideShoppingListDao(database: AppDatabase): ShoppingListDao {
        return database.shoppingListDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideUsersDao(database: AppDatabase): UsersDao {
        return database.usersDao()
    }
}