package ru.practicum.shoppinglist.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Long = 0,
    @ColumnInfo(name = "access_token")
    var accessToken: String,
    @ColumnInfo(name = "refresh_token")
    var refreshToken: String,
    @ColumnInfo(name = "last_token_update")
    val lastTokenUpdate: Long
)
