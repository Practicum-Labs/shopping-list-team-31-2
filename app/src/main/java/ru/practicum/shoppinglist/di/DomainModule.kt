package ru.practicum.shoppinglist.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.practicum.shoppinglist.domain.impl.ShoppingListInteractorImpl
import ru.practicum.shoppinglist.domain.repository.ShoppingListInteractor

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    fun bindShoppingListInteractor(
        shoppingListInteractorImpl: ShoppingListInteractorImpl
    ): ShoppingListInteractor

}
