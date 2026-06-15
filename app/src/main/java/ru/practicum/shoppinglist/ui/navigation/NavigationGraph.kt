package ru.practicum.shoppinglist.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.practicum.shoppinglist.ui.authorization.AuthorizationScreen
import ru.practicum.shoppinglist.ui.list.ListScreen
import ru.practicum.shoppinglist.ui.main.MainScreen
import ru.practicum.shoppinglist.ui.onboard.OnboardScreen
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
                login = { userId ->
                    navController.navigate("${Routes.MAIN}/$userId") {
                        popUpTo(Routes.AUTHORIZATION) { inclusive = true }

                    }
                },
                registration = { navController.navigate(Routes.REGISTRATION) },
                recoverPassword = { navController.navigate(Routes.RECOVER_PASSWORD) }
            )
        }

        composable(Routes.REGISTRATION) {
            RegistrationScreen(
                backToAuth = {
                    navController.navigate(Routes.AUTHORIZATION) {
                        popUpTo(Routes.REGISTRATION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECOVER_PASSWORD) {
            RecoverPassword(
                backToAuth = {
                    navController.navigate(Routes.AUTHORIZATION) {
                        popUpTo(Routes.RECOVER_PASSWORD) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.MAIN}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) {
            MainScreen(
                onListClick = { listId, listName ->
                    navController.navigate("${Routes.LIST}/$listId/${Uri.encode(listName)}")
                }
            )
        }

            composable("${Routes.LIST}/{listId}/{listName}") { backStackEntry ->
                val listId = backStackEntry.arguments?.getString("listId")?.toLongOrNull() ?: 0L
                val listName = backStackEntry.arguments?.getString("listName")?.let {
                    Uri.decode(it)
                } ?: ""
                ListScreen(
                    listId = listId,
                    listName = listName,
                    onBack = { navController.popBackStack() }
                )
            }

        }
    }
