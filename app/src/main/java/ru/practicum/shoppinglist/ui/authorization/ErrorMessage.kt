package ru.practicum.shoppinglist.ui.authorization

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessage(
    isError: Boolean,
    errorMessage: String) {
    if (isError) {
        Spacer(modifier = Modifier.height(11.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(11.dp))
    } else {
        Spacer(modifier = Modifier.height(40.dp))
    }
}