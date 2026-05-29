package ru.practicum.shoppinglist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ru.practicum.shoppinglist.data.repository.ShoppingListRepositoryImpl
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideShoppingListRepository(
        impl: ShoppingListRepositoryImpl
    ): ShoppingListRepository = impl
}