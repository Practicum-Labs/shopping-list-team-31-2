package ru.practicum.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.practicum.shoppinglist.data.local.entities.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE list_id = :listId ORDER BY order_position ASC")
    fun getProductsByListId(listId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("DELETE FROM products WHERE list_id = :listId")
    suspend fun deleteProductsByListId(listId: Long)

    @Query("UPDATE products SET is_purchased = :isPurchased WHERE id = :id")
    suspend fun updatePurchasedStatus(id: Long, isPurchased: Boolean)
}