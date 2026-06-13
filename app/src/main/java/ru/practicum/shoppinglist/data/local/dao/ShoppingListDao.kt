package ru.practicum.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.data.local.entities.ShoppingListEntity

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists")
    fun getAllLists(): Flow<List<ShoppingListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Query("UPDATE shopping_lists SET icon = :icon WHERE id = :id")
    suspend fun updateIcon(id: Long, icon: Int)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteListById(id: Long)

    @Query("DELETE FROM shopping_lists")
    suspend fun delete()

    @Query("UPDATE shopping_lists SET name = :newName WHERE id = :id")
    suspend fun renameList(id: Long, newName: String)

}