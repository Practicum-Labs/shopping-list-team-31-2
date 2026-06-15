package ru.practicum.shoppinglist.data.model

enum class NetworkState(val value: String) {
    IncorrectEmail("Incorrect email"),
    ShortPassword("Password should be more than 7 symbols"),
    Unauthorized("Unauthorized"),
    Conflict("user with this email already exist"),
    UnexpectedError("Unexpected error"),
    NoConnection("Check Internet connection")
}
