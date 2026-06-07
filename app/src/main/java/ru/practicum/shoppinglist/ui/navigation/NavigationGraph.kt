package ru.practicum.shoppinglist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.practicum.shoppinglist.ui.list.ListScreen
import ru.practicum.shoppinglist.ui.main.MainScreen
import ru.practicum.shoppinglist.ui.onboard.OnboardScreen
import ru.practicum.shoppinglist.ui.registration.LoginScreen
import ru.practicum.shoppinglist.ui.registration.SignUpScreen

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

        composable(Routes.LOGIN) {
            LoginScreen(
                onMain = { navController.navigate(Routes.MAIN) },
                onSignUp = { navController.navigate(Routes.SIGN_UP) }
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onList = { navController.navigate(Routes.LIST) },
                onBack = { navController.navigate(Routes.MAIN) }
            )
        }

        composable(Routes.LIST) {
            ListScreen()
        }
    }
}
