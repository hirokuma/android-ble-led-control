package work.hirokuma.bleledcontrol.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.hirokuma.bleledcontrol.ui.screens.ControlScreen
import work.hirokuma.bleledcontrol.ui.screens.ScanScreen
import work.hirokuma.bleledcontrol.ui.screens.ScanViewModel

private const val TAG = "Navigation"

enum class NavRoute {
    Scan,
    Control,
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val scanViewModel: ScanViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = NavRoute.Scan.name) {
        composable(NavRoute.Scan.name) {
            ScanScreen(
                scanViewModel = scanViewModel,
                navControlScreen = {
                    navController.navigate(NavRoute.Control.name)
                }
            )
        }
        composable(NavRoute.Control.name) {
            ControlScreen(
                scanViewModel = scanViewModel,
                navBackScreen = { navController.popBackStack() }
            )
        }
    }
}
