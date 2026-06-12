@file:Suppress("MagicNumber")

package ru.practicum.shoppinglist.ui.onboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.navigation.Routes
import ru.practicum.shoppinglist.ui.theme.ShoppingListTheme

@Composable
fun OnboardScreen(
    navController: NavHostController
) {
    var navigateToScreen by remember { mutableStateOf(false) }

    LaunchedEffect(navigateToScreen) {
        if (navigateToScreen) {
            navigateToList(navController = navController)
            navigateToScreen = false
        }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        if (!navigateToScreen) {
            navigateToScreen = true
        }
    }

    ShoppingListTheme() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .clickable { navigateToScreen = true }
            ) {
                Spacer(modifier = Modifier.padding(top = 16.dp))

                TitleScreen()

                IllustrationScreen(
                    image = R.drawable.ic_main_screen,
                    title = R.string.welcome_shopping_list,
                    description = R.string.welcome_description
                )
            }
        }
    }

}

private fun navigateToList(navController: NavHostController) {
    navController.navigate(Routes.MAIN) {
        popUpTo(Routes.ONBOARD) { inclusive = true }
    }
}

@Composable
private fun TitleScreen() {
    Row(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(74.dp)
            .padding(horizontal = 36.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Icon(
            modifier = Modifier
                .padding(top = 20.dp),
            painter = painterResource(id = R.drawable.ic_main_title),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun IllustrationScreen(
    @DrawableRes image: Int,
    @StringRes title: Int = 0,
    @StringRes description: Int = 0,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 44.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            painter = painterResource(id = image),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(top = 48.dp),
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardScreenPreview() {
    OnboardScreen(navController = rememberNavController())
}