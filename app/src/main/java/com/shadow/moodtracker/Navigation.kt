package com.shadow.moodtracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.presentationLayer.screens.AnxietyTracker
import com.shadow.moodtracker.presentationLayer.screens.CoffeeTrackerScreen
import com.shadow.moodtracker.presentationLayer.screens.HabitPage
import com.shadow.moodtracker.presentationLayer.screens.HighlightOfTheDaysPage
import com.shadow.moodtracker.presentationLayer.screens.HomePage
import com.shadow.moodtracker.presentationLayer.screens.LoginPage
import com.shadow.moodtracker.presentationLayer.screens.RateMyDay
import com.shadow.moodtracker.presentationLayer.screens.SeriesDetailsScreen
import com.shadow.moodtracker.presentationLayer.screens.SeriesTrackerScreen
import com.shadow.moodtracker.presentationLayer.screens.SignUpPage
import com.shadow.moodtracker.presentationLayer.screens.StepTrackerPage
import com.shadow.moodtracker.viewmodel.AuthState
import com.shadow.moodtracker.viewmodel.AuthViewModel
import com.shadow.moodtracker.viewmodel.SeriesTrackerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    // Observe authentication state
    val authState = authViewModel.authState.collectAsState().value

    // Show loading state if authState is still loading or null
    if (authState is AuthState.LoadingState) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            LoadingCat()
        }

        return
    }
    val startDestination = if (authState is AuthState.Authenticated) "homepage" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUpPage(modifier, navController, authViewModel)
        }
        composable("homepage") {
            //   HomePage(modifier, navController, authViewModel)
            HomePage(navController, authViewModel)

        }
        composable("wheel_habits") {
            HabitPage(
                navControllers = navController,
            )
        }

        composable("RateMyDay") {
            RateMyDay(navController=navController)
        }
        composable("StepTracker") {
            StepTrackerPage(navController=navController)
        }
        composable("AnxietyTracker") {
            AnxietyTracker(navController=navController)
        }
        composable("HighlightOfTheDay") {
            HighlightOfTheDaysPage(
                navController = navController
            )
        }


        ////////////////////////////////////////////////////////////////
        composable("seriesTracker") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("seriesTracker")
            }
            val viewModel: SeriesTrackerViewModel = viewModel(parentEntry)

            SeriesTrackerScreen(
                viewModel = viewModel,
                onSeriesClick = { id, _ ->
                    navController.navigate("seriesDetails/$id")
                },
                navController = navController
            )
        }

        composable(
            "seriesDetails/{seriesId}",
            arguments = listOf(navArgument("seriesId") { type = NavType.StringType })
        ) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getString("seriesId") ?: return@composable

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("seriesTracker")
            }
            val viewModel: SeriesTrackerViewModel = viewModel(parentEntry)

            SeriesDetailsScreen(
                seriesId = seriesId,
                viewModel = viewModel,
                navController = navController

            )
        }


        //////////////////////////////////////////
        composable("coffeeTracker") {
            CoffeeTrackerScreen(
                navController = navController
            )
        }
    }
}










