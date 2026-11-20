package com.devilgamehub.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.devilgamehub.data.model.Categories
import com.devilgamehub.ui.screens.favorites.FavoritesScreen
import com.devilgamehub.ui.screens.favorites.FavoritesViewModel
import com.devilgamehub.ui.screens.home.HomeScreen
import com.devilgamehub.ui.screens.home.HomeViewModel
import com.devilgamehub.ui.screens.library.LibraryScreen
import com.devilgamehub.ui.screens.library.LibraryViewModel
import com.devilgamehub.ui.screens.player.GamePlayerScreen
import com.devilgamehub.ui.screens.player.GamePlayerViewModel
import com.devilgamehub.ui.screens.settings.SettingsScreen
import com.devilgamehub.ui.screens.settings.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = viewModel,
                onPlayGame = { game ->
                    navController.navigate(Screen.Player.createRoute(game.id))
                },
                onNavigateToLibrary = { category ->
                    val categoryId = category?.id ?: Categories.ALL.id
                    navController.navigate("${Screen.Library.route}?categoryId=$categoryId")
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(
            route = "${Screen.Library.route}?categoryId={categoryId}",
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            val initialCategory = categoryId?.let { id ->
                Categories.list.find {
                    it.id.equals(id, ignoreCase = true) || it.name.equals(id, ignoreCase = true)
                }
            }

            val viewModel: LibraryViewModel = viewModel()
            LibraryScreen(
                viewModel = viewModel,
                onPlayGame = { game ->
                    navController.navigate(Screen.Player.createRoute(game.id))
                },
                initialCategory = initialCategory
            )
        }

        composable(Screen.Favorites.route) {
            val viewModel: FavoritesViewModel = viewModel()
            FavoritesScreen(
                viewModel = viewModel,
                onPlayGame = { game ->
                    navController.navigate(Screen.Player.createRoute(game.id))
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel()
            SettingsScreen(viewModel = viewModel)
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            val viewModel: GamePlayerViewModel = viewModel()

            GamePlayerScreen(
                gameId = gameId,
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
