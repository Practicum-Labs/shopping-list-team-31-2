package ru.practicum.shoppinglist.ui.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview(showSystemUi = true)
@Composable
fun LoginScreen(
    onSignUp: () -> Unit = {},
    onMain: () -> Unit = {},
    ) {
    var value by remember { mutableStateOf("") }
    ShoppingListTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Добро пожаловать!",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 50.dp),
                style = MaterialTheme.typography.headlineSmall
            )

            AuthField(
                label = "E-mail",
                value = value,
                onValueChange = { value = it },
                placeholder = "Введите e-mail"
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(
                label = "Пароль",
                value = value,
                onValueChange = { value = it },
                placeholder = "Введите пароль"
            )
            Spacer(modifier = Modifier.height(40.dp))
            ShoppingListsButton()
            Spacer(modifier = Modifier.height(40.dp))
            Row() {
                Text(
                    text = "Восстановить пароль", color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Регистрация", color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ShoppingListsButton(
    onClick: () -> Unit = {}
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary),
    ) {
        Text(
            text = "Войти", color = Color.Black, modifier = Modifier
        )
    }
}

@Composable
fun AuthField(
    label: String, value: String, onValueChange: (String) -> Unit = {}, placeholder: String
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledBorderColor = MaterialTheme.colorScheme.secondary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.secondary
        )
    )
}
