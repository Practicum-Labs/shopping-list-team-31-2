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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.authorization.AuthField
import ru.practicum.shoppinglist.ui.authorization.ErrorMessage
import ru.practicum.shoppinglist.ui.authorization.ShoppingListsButton
import ru.practicum.shoppinglist.ui.navigation.ActionBack
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.registration.viewmodel.RegistrationIntent
import ru.practicum.shoppinglist.ui.registration.viewmodel.RegistrationViewModel
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreen(
    backToAuth: () -> Unit = {},
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.user) {
        if (state.user != null) {
            backToAuth()
        }
    }

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
                    value = state.currentEmail,
                    onValueChange = { newEmail ->
                        viewModel.processIntent(RegistrationIntent.SetCurrentEmail(newEmail))
                    },
                    placeholder = stringResource(R.string.enter_email),
                    isPassword = false
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthField(
                    label = stringResource(R.string.password),
                    value = state.currentPassword,
                    onValueChange = { newPassword ->
                        viewModel.processIntent(RegistrationIntent.SetCurrentPassword(newPassword))
                    },
                    placeholder = stringResource(R.string.enter_password),
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthField(
                    label = stringResource(R.string.one_more_password),
                    value = state.currentRepeatPassword,
                    onValueChange = { newRepeatPassword ->
                        viewModel.processIntent(
                            RegistrationIntent.SetCurrentRepeatPassword(
                                newRepeatPassword
                            )
                        )
                    },
                    placeholder = stringResource(R.string.enter_one_more_password),
                    isPassword = true
                )
                ErrorMessage(
                    isError = state.errorMessage != null,
                    errorMessage = state.errorMessage?.let { stringResource(it) }
                )
                ShoppingListsButton(
                    buttonName = stringResource(R.string.register),
                    enabled = state.isRegistrationActive,
                    onClick = {
                        viewModel.processIntent(intent = RegistrationIntent.Registration)
                    }
                )
            }
        }
    }
}
