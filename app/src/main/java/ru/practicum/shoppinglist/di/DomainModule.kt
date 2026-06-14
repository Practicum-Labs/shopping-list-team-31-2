package ru.practicum.shoppinglist.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.practicum.shoppinglist.domain.impl.AuthInteractorImpl
import ru.practicum.shoppinglist.domain.impl.ShoppingListInteractorImpl
import ru.practicum.shoppinglist.domain.repository.AuthInteractor
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
    abstract fun bindAuthInteractor(
        impl: AuthInteractorImpl
    ): AuthInteractor
}
