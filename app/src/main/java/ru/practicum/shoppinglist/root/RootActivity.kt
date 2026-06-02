package ru.practicum.shoppinglist.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ru.practicum.shoppinglist.ui.navigation.NavigationGraph

class RootActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavigationGraph(
                navController = navController
            )
        }
    }
}
