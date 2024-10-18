package work.hirokuma.bleledcontrol

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import work.hirokuma.bleledcontrol.data.BleScan
import work.hirokuma.bleledcontrol.ui.DeviceScreen
import work.hirokuma.bleledcontrol.ui.theme.AppTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var bleScan: BleScan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        bleScan = BleScan(bluetoothLeScanner)

        val chk = checkPermission()
        if (!chk) {
            val reqPerm = requestPermission()
            requestPermissionLauncher.launch(reqPerm)
//            return
        }

        setContent {
            AppTheme {
                val compScope = rememberCoroutineScope()
                var counter by remember { mutableIntStateOf(0) }
                val list = listOf<String>()
                val myList by remember { mutableStateOf(list) }
                DeviceScreen(myList, {
                    counter++
                    val nowCounter = counter
                    Log.d(TAG, "click: $counter")
                    compScope.launch(Dispatchers.IO) {
                        Log.d(TAG, "toString: $this")
                        while (true) {
                            delay(3000L)
                            Log.d(TAG, "無限: nowCounter=$nowCounter, counter=$counter")
                        }
                    }
                })
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d(TAG, "requestPermissionLauncher: isGranted")
                bleScan.scanLeDevice()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d(TAG, "requestPermissionLauncher: !isGranted")
            }
        }

    private fun requestPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_SCAN
        } else {
            Manifest.permission.ACCESS_FINE_LOCATION
        }
    }

    private fun checkPermission(): Boolean {
        val reqPerm = requestPermission()
        when {
            ContextCompat.checkSelfPermission(
                this, reqPerm
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.d(TAG, "許可あり")
                return true
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
}
