package ru.practicum.shoppinglist.ui.recoverpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import ru.practicum.shoppinglist.ui.authorization.AuthButton
import ru.practicum.shoppinglist.ui.authorization.AuthField
import ru.practicum.shoppinglist.ui.authorization.ErrorMessage
import ru.practicum.shoppinglist.ui.navigation.ActionBack
import ru.practicum.shoppinglist.ui.navigation.AppBarTop
import ru.practicum.shoppinglist.ui.recoverpassword.viewmodel.RecoverPasswordIntent
import ru.practicum.shoppinglist.ui.recoverpassword.viewmodel.RecoverPasswordViewModel
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Preview(showSystemUi = true)
@Composable
fun RecoverPassword(
    backToAuth: () -> Unit = {},
    viewModel: RecoverPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isReadyBackToAuth) {
        if (state.isReadyBackToAuth) {
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
                    .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.recovering_password),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 50.dp),
                    style = MaterialTheme.typography.headlineSmall
                )

                AuthField(
                    label = stringResource(R.string.email),
                    value = state.currentEmail,
                    onValueChange = { newEmail ->
                        viewModel.processIntent(RecoverPasswordIntent.SetCurrentEmail(newEmail))
                    },
                    placeholder = stringResource(R.string.enter_email),
                    isPassword = false
                )
                ErrorMessage(
                    isError = state.errorMessage != null,
                    errorMessage = state.errorMessage?.let { stringResource(it) }
                )
                AuthButton(
                    buttonName = stringResource(R.string.send_email_for_recover),
                    enabled = state.isRecoverPasswordActive,
                    onClick = {
                        viewModel.processIntent(intent = RecoverPasswordIntent.RecoverPassword)
                    }
                )
            }
        }
    }
}
