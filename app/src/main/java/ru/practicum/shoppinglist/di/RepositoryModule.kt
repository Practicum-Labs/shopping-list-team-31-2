package ru.practicum.shoppinglist.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.practicum.shoppinglist.data.repository.AuthRepositoryImpl
import ru.practicum.shoppinglist.data.repository.ProductRepositoryImpl
import ru.practicum.shoppinglist.data.repository.ShoppingListRepositoryImpl
import ru.practicum.shoppinglist.domain.repository.AuthRepository
import ru.practicum.shoppinglist.domain.repository.ProductRepository
import ru.practicum.shoppinglist.domain.repository.ShoppingListRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideShoppingListRepository(
        impl: ShoppingListRepositoryImpl
    ): ShoppingListRepository = impl

    @Provides
    @Singleton
    fun provideProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository = impl

}