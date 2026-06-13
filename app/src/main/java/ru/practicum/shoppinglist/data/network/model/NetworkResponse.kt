package ru.practicum.shoppinglist.data.network.model

open class NetworkResponse {
    var resultCode = 0
    var data: Any? = null

    companion object {
        const val OK_RESULT = 200
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val CONFLICT = 409
        const val INTERNAL_SERVER_ERROR = 500
        const val NO_CONNECTION = -1
    }
}