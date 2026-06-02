package ru.practicum.shoppinglist.data.network.model.response

import com.google.gson.annotations.SerializedName

data class CheckResponse(
    @SerializedName("is_valid")
    val isValid: Boolean
)