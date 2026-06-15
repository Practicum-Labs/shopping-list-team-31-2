package ru.practicum.shoppinglist.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.practicum.shoppinglist.domain.impl.AuthInteractorImpl
import ru.practicum.shoppinglist.domain.impl.ShoppingListInteractorImpl
import ru.practicum.shoppinglist.domain.repository.AuthInteractor
import ru.practicum.shoppinglist.domain.repository.ProductInteractor
import ru.practicum.shoppinglist.domain.repository.ProductInteractorImpl
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    @Singleton
    fun bindShoppingListInteractor(
        shoppingListInteractorImpl: ShoppingListInteractorImpl
    ): ShoppingListInteractor

    @Binds
    @Singleton
    fun bindAuthInteractor(
        impl: AuthInteractorImpl
    ): AuthInteractor
  
    @Binds
    @Singleton
    fun bindProductInteractor(
        productInteractorImpl: ProductInteractorImpl
    ): ProductInteractor
}
