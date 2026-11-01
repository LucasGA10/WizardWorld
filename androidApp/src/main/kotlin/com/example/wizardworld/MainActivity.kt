package com.example.wizardworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.wizardworld.navigation.Destinations
import com.example.wizardworld.ui.screens.characters.CharacterListScreen
import com.example.wizardworld.ui.screens.houses.HouseDetailScreen
import com.example.wizardworld.ui.screens.characters.CharacterDetailScreen
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
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val isHousesSelected =
        destination?.hasRoute<Destinations.HouseListRoute>() == true

    val isCharactersSelected =
        destination?.hasRoute<Destinations.CharacterListRoute>() == true

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        bottomBar = {
            if (isHousesSelected || isCharactersSelected) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    NavigationBarItem(
                        selected = isCharactersSelected,
                        onClick = {
                            navController.navigate(Destinations.CharacterListRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.nav_characters),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_characters)) },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    NavigationBarItem(
                        selected = isHousesSelected,
                        onClick = {
                            navController.navigate(Destinations.HouseListRoute) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = stringResource(R.string.nav_houses),
                            )
                        },
                        label = { Text(stringResource(R.string.nav_houses)) },
                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        },
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Destinations.CharacterListRoute,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            composable<Destinations.HouseListRoute> {
                HouseListScreen(
                    onHouseClick = { houseId ->
                        navController.navigate(
                            Destinations.HouseDetailRoute(houseId),
                        )
                    },
                )
            }

            composable<Destinations.HouseDetailRoute> { backStackEntry ->
                val route =
                    backStackEntry.toRoute<Destinations.HouseDetailRoute>()

                HouseDetailScreen(
                    houseId = route.houseId,
                    onBack = { navController.popBackStack() },
                )
            }

            composable<Destinations.CharacterListRoute> {
                CharacterListScreen(
                    onCharacterClick = { characterId ->
                        navController.navigate(
                            Destinations.CharacterDetailRoute(characterId),
                        )
                    },
                )
            }
            
            composable<Destinations.CharacterDetailRoute> { backStackEntry ->
                val route =
                    backStackEntry.toRoute<Destinations.CharacterDetailRoute>()

                CharacterDetailScreen(
                    characterId = route.characterId,
                    onBack = {
                        navController.popBackStack()
                    },
                )
            }

        }
    }
}
