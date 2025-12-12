package com.evalenzuela.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.evalenzuela.navigation.navigation.BottomBar
import com.evalenzuela.navigation.navigation.BottomNavItem
import com.evalenzuela.navigation.navigation.Routes
import com.evalenzuela.navigation.ui.screens.CartScreen
import com.evalenzuela.navigation.ui.screens.DetailScreen
import com.evalenzuela.navigation.ui.screens.HomeScreen
import com.evalenzuela.navigation.ui.screens.ProfileScreen
import com.evalenzuela.navigation.ui.theme.NavigationTheme
import com.evalenzuela.navigation.ui.viewmodel.MainViewModel
import com.evalenzuela.navigation.ui.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()


    val bottomItems = listOf(
        BottomNavItem.Home,

        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    val vm: MainViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()

    Scaffold(
        bottomBar = { BottomBar(navController, bottomItems) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(viewModel = vm, onItemClick = { id ->
                    navController.navigate(Routes.detailRoute(id))
                })
            }



            composable(Routes.CART) {
                CartScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }

            composable(Routes.PROFILE) {
                ProfileScreen(viewModel = vm)
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("itemId") ?: -1

                DetailScreen(
                    itemId = id,
                    viewModel = vm,
                    postViewModel = postViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}