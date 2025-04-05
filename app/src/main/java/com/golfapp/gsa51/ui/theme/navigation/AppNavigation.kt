package com.golfapp.gsa51.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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

import com.golfapp.gsa51.ui.theme.screens.GameDetailsScreen
import com.golfapp.gsa51.ui.theme.screens.GameRulesScreen
import com.golfapp.gsa51.ui.theme.screens.IndividualGameSettingsScreen
import com.golfapp.gsa51.ui.theme.screens.ResultsScreen
import com.golfapp.gsa51.ui.theme.screens.ScoringScreen
import com.golfapp.gsa51.ui.theme.screens.SplashScreen
import com.golfapp.gsa51.ui.theme.screens.TeamPairingsScreen
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.golfapp.gsa51.ui.theme.screens.SavedGamesScreen
import com.golfapp.gsa51.ui.theme.screens.FinalScoreDetailsScreen

// Add these imports for animations
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

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
    object GameRules : Screen("game_rules")

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
        composable(
            route = Screen.Splash.route,
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.GameDetails.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.GameDetails.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            val context = LocalContext.current
            GameDetailsScreen(
                onNavigateToIndividualSettings = { gameId ->
                    navController.navigate(
                        Screen.IndividualGameSettings.createRoute("gameId" to gameId.toString())
                    )
                },
                onExitApp = {
                    // This will finish the current activity (close the app)
                    (context as? android.app.Activity)?.finish()
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                },
                onNavigateToSavedGames = {
                    navController.navigate(Screen.SavedGames.route)
                }
            )
        }

        composable(
            route = Screen.IndividualGameSettings.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            IndividualGameSettingsScreen(
                gameId = gameId,
                onNavigateToTeamPairings = { id ->
                    navController.navigate(Screen.TeamPairings.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.SavedGames.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
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
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.TeamPairings.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            TeamPairingsScreen(
                gameId = gameId,
                onNavigateToScoring = { id ->
                    navController.navigate(Screen.Scoring.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
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
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.Scoring.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            ScoringScreen(
                gameId = gameId,
                onNavigateToResults = { id ->
                    navController.navigate(Screen.Results.createRoute("gameId" to id.toString()))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.FinalScoreDetails.route,
            arguments = listOf(navArgument("gameId") { type = NavType.LongType }),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: -1L

            FinalScoreDetailsScreen(
                gameId = gameId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGameRules = {
                    navController.navigate(Screen.GameRules.route)
                }
            )
        }

        composable(
            route = Screen.GameRules.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            GameRulesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}