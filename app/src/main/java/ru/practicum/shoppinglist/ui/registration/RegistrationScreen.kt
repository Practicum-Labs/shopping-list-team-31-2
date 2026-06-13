package ru.practicum.shoppinglist.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import ru.practicum.shoppinglist.ui.authorization.ShoppingListsButton
import ru.practicum.shoppinglist.ui.authorization.AuthField
import ru.practicum.shoppinglist.ui.authorization.ErrorMessage
import ru.practicum.shoppinglist.ui.navigation.ActionBack
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreen(
    backToAuth: () -> Unit = {}
) {
    var value by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("ERROR!") }

    ShoppingListTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            AppBarTop(
                title = "",
                back = ActionBack(isView = true, onClick = backToAuth)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.registration),
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
                Spacer(modifier = Modifier.height(16.dp))
                AuthField(
                    label = stringResource(R.string.one_more_password),
                    value = value,
                    onValueChange = { value = it },
                    placeholder = stringResource(R.string.enter_one_more_password)
                )
                ErrorMessage(isError = true, errorMessage = errorMessage)
                ShoppingListsButton(buttonName = stringResource(R.string.register))
            }
        }
    }
}
