package work.hirokuma.bleledcontrol.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import work.hirokuma.bleledcontrol.ui.theme.AppTheme

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO Composableに実装し直す
        val chk = checkPermissions()
        if (!chk) {
            val reqPerm = requestPermissions()
            requestPermissionLauncher.launch(reqPerm)
        }

        setContent {
            AppTheme {
                MainNavigation()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted: Map<String, @JvmSuppressWildcards Boolean> ->
            if (isGranted.containsValue(true)) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d(TAG, "requestPermissionLauncher: isGranted")
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d(TAG, "requestPermissionLauncher: !isGranted")
            }
        }

    private fun requestPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun checkPermissions(): Boolean {
        val reqPerms = requestPermissions()
        for (reqPerm in reqPerms) {
            when {
                ContextCompat.checkSelfPermission(
                    this, reqPerm
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    Log.d(TAG, "許可あり")
//                    return true
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, reqPerm
                ) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected, and what
                    // features are disabled if it's declined. In this UI, include a
                    // "cancel" or "no thanks" button that lets the user continue
                    // using your app without granting the permission.
                    Log.d(TAG, "UIで説明しないといけないらしい")
                    return false
                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    Log.d(TAG, "たずねよう")
                    return false
                }
            }
        }
        Log.e(TAG, "最後まですすんだ")
        return true
    }
}
