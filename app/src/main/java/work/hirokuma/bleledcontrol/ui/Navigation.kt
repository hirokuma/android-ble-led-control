package work.hirokuma.bleledcontrol.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import work.hirokuma.bleledcontrol.ui.screens.ControlScreen
import work.hirokuma.bleledcontrol.ui.screens.ScanScreen

private const val TAG = "Navigation"

enum class NavRoute {
    Scan,
    Control,
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.Scan.name) {
        composable(NavRoute.Scan.name) {
            ScanScreen(
                navControlScreen = {
                    Log.d(TAG, "ScanScreen(${it.address})")
                    navController.navigate("${NavRoute.Control.name}/${it.address}")
                }
            )
        }
        composable(
            "${NavRoute.Control.name}/{deviceAddress}",
            arguments = listOf(
                navArgument("deviceAddress") {
                    type = NavType.StringType
                }
            )
        ) {
            ControlScreen(
                deviceAddress = it.arguments?.getString("deviceAddress") ?: "not found",
                navBackScreen = { navController.popBackStack() }
            )
        }
    }
}
