package work.hirokuma.bleledcontrol.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.hirokuma.bleledcontrol.ui.screens.ControlScreen
import work.hirokuma.bleledcontrol.ui.screens.ScanScreen
import work.hirokuma.bleledcontrol.ui.screens.LbsViewModel

private const val TAG = "Navigation"

enum class NavRoute {
    Scan,
    Control,
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val lbsViewModel: LbsViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = NavRoute.Scan.name) {
        composable(NavRoute.Scan.name) {
            ScanScreen(
                lbsViewModel = lbsViewModel,
                onItemClicked = { device ->
                    Log.d(TAG, "ScanScreen.onItemClicked")
                    lbsViewModel.connectDevice(device)
                    navController.navigate(NavRoute.Control.name)
                }
            )
        }
        composable(NavRoute.Control.name) {
            ControlScreen(
                lbsViewModel = lbsViewModel,
                onBackButtonClicked = {
                    lbsViewModel.disconnectDevice()
                    navController.popBackStack()
                }
            )
        }
    }
}
