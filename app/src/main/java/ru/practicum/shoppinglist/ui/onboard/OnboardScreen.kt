package ru.practicum.shoppinglist.ui.onboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.ui.navigation.Routes

@Composable
fun OnboardScreen(
    navController: NavHostController
) {

    LaunchedEffect(Unit) {
        delay(3000); navController.navigate(Routes.MAIN) {
        popUpTo(Routes.ONBOARD) { inclusive = true }
    }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(
            text = stringResource(R.string.shopping_list),
            modifier = Modifier
                .padding(bottom = 94.dp)
                .padding(horizontal = 44.dp),

            )

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 44.dp),
            painter = painterResource(R.drawable.ic_main_screen),
            contentDescription = null
        )

    }
}
