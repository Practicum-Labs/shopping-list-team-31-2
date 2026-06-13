package ru.practicum.shoppinglist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.practicum.shoppinglist.ui.list.ListScreen
import ru.practicum.shoppinglist.ui.main.MainScreen
import ru.practicum.shoppinglist.ui.onboard.OnboardScreen
import ru.practicum.shoppinglist.ui.authorization.AuthorizationScreen
import ru.practicum.shoppinglist.ui.recoverpassword.RecoverPassword
import ru.practicum.shoppinglist.ui.registration.RegistrationScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARD,
        modifier = modifier
    ) {
        composable(Routes.ONBOARD) {
            OnboardScreen(navController = navController)
        }

        composable(Routes.AUTHORIZATION) {
            AuthorizationScreen(
                login = { navController.navigate(Routes.MAIN) },
                registration = { navController.navigate(Routes.REGISTRATION) },
                recoverPassword = {navController.navigate(Routes.RECOVER_PASSWORD)}
            )
        }

        composable(Routes.REGISTRATION) {
            RegistrationScreen(
                backToAuth = { navController.navigate(Routes.AUTHORIZATION) }
            )
        }

        composable(Routes.RECOVER_PASSWORD) {
            RecoverPassword(
                backToAuth = { navController.navigate(Routes.AUTHORIZATION) }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onListClick = { navController.navigate(Routes.LIST) }
            )
        }

        composable(Routes.LIST) {
            ListScreen()
        }
    }
}
