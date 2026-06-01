package ru.practicum.shoppinglist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.practicum.shoppinglist.ui.main.MainScreen
import ru.practicum.shoppinglist.ui.onboard.OnboardScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARD,
        modifier = modifier
    ){
        composable(Routes.ONBOARD){
            OnboardScreen( navController = navController)
        }

        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}