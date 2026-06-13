package ru.practicum.shoppinglist.ui.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview(showSystemUi = true)
@Composable
fun AuthorizationScreen(
    registration: () -> Unit = {},
    recoverPassword: () -> Unit = {},
    login: () -> Unit = {}
    ) {
    var value by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("ERROR!") }
    ShoppingListTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp)
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.hello),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 50.dp),
                style = MaterialTheme.typography.headlineSmall
            )

            AuthField(
                label = stringResource(R.string.email),
                value = value,
                onValueChange = { value = it },
                placeholder = stringResource(R.string.enter_email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(
                label = stringResource(R.string.password),
                value = value,
                onValueChange = { value = it },
                placeholder = stringResource(R.string.enter_password)
            )
            ErrorMessage(isError = true, errorMessage = errorMessage)
            ShoppingListsButton(
                buttonName = stringResource(R.string.login),
                onClick = login
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row() {
                Text(
                    text = stringResource(R.string.recover_password),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable(
                        onClick = recoverPassword
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.register),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable(
                        onClick = registration
                    )
                )
            }
        }
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
