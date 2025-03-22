package com.golfapp.gsa51.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.golfapp.gsa51.ui.theme.screens.FinalScoreDetailsScreen
import com.golfapp.gsa51.ui.theme.screens.GameDetailsScreen
import com.golfapp.gsa51.ui.theme.screens.IndividualGameSettingsScreen
import com.golfapp.gsa51.ui.theme.screens.ResultsScreen
import com.golfapp.gsa51.ui.theme.screens.ScoringScreen
import com.golfapp.gsa51.ui.theme.screens.SplashScreen
import com.golfapp.gsa51.ui.theme.screens.TeamPairingsScreen
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.golfapp.gsa51.ui.theme.screens.SavedGamesScreen


// Define navigation routes
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object GameDetails : Screen("game_details")
    object IndividualGameSettings : Screen("individual_game_settings/{gameId}")
    object TeamPairings : Screen("team_pairings/{gameId}")
    object Scoring : Screen("scoring/{gameId}")
    object Results : Screen("results/{gameId}")
    object FinalScoreDetails : Screen("final_score_details/{gameId}")
    object SavedGames : Screen("saved_games")

    fun createRoute(vararg args: Pair<String, String>): String {
        var result = route
        args.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.GameDetails.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.GameDetails.route) {
            GameDetailsScreen(
                onNavigateToIndividualSettings = { gameId ->
                    navController.navigate(
                        Screen.IndividualGameSettings.createRoute("gameId" to gameId.toString())
                    )
                },
                onExitApp = {
                    // Handle exit app action
                },
                onNavigateToGameRules = {
                    // Handle navigation to game rules
                },
                onNavigateToSavedGames = {
                    navController.navigate(Screen.SavedGames.route)
                }
            )
        }

        composable(
            route = Screen.IndividualGameSettings.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            IndividualGameSettingsScreen(
                gameId = gameId,
                onNavigateToTeamPairings = { id ->
                    navController.navigate(Screen.TeamPairings.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.SavedGames.route) {
            SavedGamesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResumeGame = { gameId ->
                    // Navigate to scoring screen with the game ID
                    navController.navigate(Screen.Scoring.createRoute("gameId" to gameId.toString()))
                },
                onNewGame = {
                    // Navigate to game details screen
                    navController.navigate(Screen.GameDetails.route) {
                        popUpTo(Screen.SavedGames.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.TeamPairings.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            TeamPairingsScreen(
                gameId = gameId,
                onNavigateToScoring = { id ->
                    navController.navigate(Screen.Scoring.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            ResultsScreen(
                gameId = gameId,
                onNavigateToScoreDetails = { id ->
                    navController.navigate(Screen.FinalScoreDetails.createRoute("gameId" to id.toString()))
                },
                onNavigateToNewGame = {
                    navController.navigate(Screen.GameDetails.route) {
                        popUpTo(Screen.GameDetails.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
                // Note: onShareResults is now handled directly in the ResultsScreen
            )
        }
        composable(
            route = Screen.Scoring.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            ScoringScreen(
                gameId = gameId,
                onNavigateToResults = { id ->
                    navController.navigate(Screen.Results.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.FinalScoreDetails.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            FinalScoreDetailsScreen(
                gameId = gameId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            ResultsScreen(
                gameId = gameId,
                onNavigateToScoreDetails = { id ->
                    navController.navigate(Screen.FinalScoreDetails.createRoute("gameId" to id.toString()))
                },
                onNavigateToNewGame = {
                    navController.navigate(Screen.GameDetails.route) {
                        popUpTo(Screen.GameDetails.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}