package com.ifscfinder.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ifscfinder.presentation.ui.screens.*

sealed class IfscRoute(val route: String) {
    data object Search : IfscRoute("search")
    data object Banks : IfscRoute("banks")
    data object Favorites : IfscRoute("favorites")
    data object Detail : IfscRoute("detail/{ifsc}") {
        fun create(ifsc: String) = "detail/$ifsc"
    }
    data object BankBranches : IfscRoute("bank/{bankName}") {
        fun create(bankName: String) = "bank/${bankName.replace("/", "%2F")}"
    }
}

@Composable
fun IfscNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(IfscRoute.Search.route, IfscRoute.Banks.route, IfscRoute.Favorites.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(
                        Triple(IfscRoute.Search.route, Icons.Default.Search, "Search"),
                        Triple(IfscRoute.Banks.route, Icons.Default.AccountBalance, "Banks"),
                        Triple(IfscRoute.Favorites.route, Icons.Default.Star, "Favorites")
                    ).forEach { (route, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, null) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = IfscRoute.Search.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(IfscRoute.Search.route) {
                IfscSearchScreen(onBranchClick = { navController.navigate(IfscRoute.Detail.create(it)) })
            }
            composable(IfscRoute.Banks.route) {
                IfscBankBrowseScreen(onBankClick = { navController.navigate(IfscRoute.BankBranches.create(it)) })
            }
            composable(IfscRoute.Favorites.route) {
                IfscFavoritesScreen(onBranchClick = { navController.navigate(IfscRoute.Detail.create(it)) })
            }
            composable(
                IfscRoute.Detail.route,
                arguments = listOf(navArgument("ifsc") { type = NavType.StringType })
            ) { entry ->
                IfscDetailScreen(
                    ifsc = entry.arguments?.getString("ifsc").orEmpty(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                IfscRoute.BankBranches.route,
                arguments = listOf(navArgument("bankName") { type = NavType.StringType })
            ) { entry ->
                IfscBankBranchesScreen(
                    bankName = entry.arguments?.getString("bankName").orEmpty().replace("%2F", "/"),
                    onBranchClick = { navController.navigate(IfscRoute.Detail.create(it)) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
