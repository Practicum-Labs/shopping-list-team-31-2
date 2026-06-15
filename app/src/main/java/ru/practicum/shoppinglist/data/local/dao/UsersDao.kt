package ru.practicum.shoppinglist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.practicum.shoppinglist.data.local.entities.UserEntity

@Dao
interface UsersDao {
    @Query("SELECT * FROM users WHERE refresh_token = :userId")
    fun getUserById(userId: Long): UserEntity

    @Query("SELECT * FROM users WHERE refresh_token = :refreshToken")
    fun getUserByRefreshToken(refreshToken: String): UserEntity

    @Query("SELECT * FROM users WHERE access_token = :accessToken")
    fun getUserByAccessToken(accessToken: String): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrationUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}