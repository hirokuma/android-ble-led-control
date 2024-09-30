package work.hirokuma.bleledcontrol

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import work.hirokuma.bleledcontrol.ui.theme.BLELEDControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLELEDControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    scanLeDevice()
                    Greeting("Android")
                }
            }
        }
    }

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 10 seconds.
    private val scanPeriod: Long = 10000

    private fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            Log.d(TAG, "scanLeDevice: !scanning")
            handler.postDelayed({
                scanning = false
                val reqPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Manifest.permission.BLUETOOTH_SCAN
                } else {
                    Manifest.permission.ACCESS_FINE_LOCATION
                }
                when {
                    ContextCompat.checkSelfPermission(
                        this, reqPerm
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // You can use the API that requires the permission.
                        Log.d(TAG, "許可あり")
                        scanning = true
                        Log.d(TAG, "scanLeDevice: startScan")
                        bluetoothLeScanner.startScan(leScanCallback)
                    }
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.BLUETOOTH_SCAN
                    ) -> {
                        // In an educational UI, explain to the user why your app requires this
                        // permission for a specific feature to behave as expected, and what
                        // features are disabled if it's declined. In this UI, include a
                        // "cancel" or "no thanks" button that lets the user continue
                        // using your app without granting the permission.
                        Log.d(TAG, "UIで説明しないといけないらしい")
                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        Log.d(TAG, "たずねよう")
                        requestPermissionLauncher.launch(reqPerm)
                    }
                }
//                Log.d(TAG, "scanLeDevice: stopScan")
//                bluetoothLeScanner.stopScan(leScanCallback)
            }, scanPeriod)
        } else {
            scanning = false
            Log.d(TAG, "scanLeDevice: stopScan 2")
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d(TAG, "requestPermissionLauncher: isGranted")
                bluetoothLeScanner.startScan(leScanCallback)
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d(TAG, "requestPermissionLauncher: !isGranted")
            }
        }


    //    private val leDeviceListAdapter = LeDeviceListAdapter()
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(TAG, "onScanResult: ${result.device}")
//            leDeviceListAdapter.addDevice(result.device)
//            leDeviceListAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private val TAG = javaClass.simpleName
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BLELEDControlTheme {
        Greeting("Android")
    }
}