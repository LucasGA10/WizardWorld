package com.example.wizardworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.wizardworld.navigation.Destinations
import com.example.wizardworld.ui.screens.houses.HouseDetailScreen
import com.example.wizardworld.ui.screens.houses.HouseListScreen
import com.example.wizardworld.ui.theme.WizardWorldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WizardWorldTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HouseListRoute,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<Destinations.HouseListRoute> {
            HouseListScreen(
                onHouseClick = { houseId ->
                    navController.navigate(Destinations.HouseDetailRoute(houseId))
                },
            )
        }
        composable<Destinations.HouseDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<Destinations.HouseDetailRoute>()
            HouseDetailScreen(
                houseId = route.houseId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
