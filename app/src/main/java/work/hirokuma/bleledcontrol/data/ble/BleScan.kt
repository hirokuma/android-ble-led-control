package work.hirokuma.bleledcontrol.data.ble

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

private const val TAG = "BleScan"

class BleScan(context: Context) {
    private val bluetoothLeScanner: BluetoothLeScanner
    init {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    var scanning = false
        private set

    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 1 seconds.
    private val scanPeriod: Long = 1000

    private var resultCallback: ((String) -> Unit)? = null

    fun startScan(resultCallback: ((String) -> Unit)?): Boolean {
        if (scanning) {
            Log.d(TAG, "already scanning")
            return false
        }
        try {
            Log.d(TAG, "scanLeDevice: !scanning")
            handler.postDelayed({
                Log.d(TAG, "scanLeDevice: startScan")
                bluetoothLeScanner.startScan(leScanCallback)
            }, scanPeriod)
            this.resultCallback = resultCallback
            scanning = true
        }
        catch (e: SecurityException) {
            Log.e(TAG, "startScan: $e")
            return false
        }
        return true
    }

    fun stopScan(): Boolean {
        if (!scanning) {
            Log.d(TAG, "not scanning")
            return false
        }
        try {
            bluetoothLeScanner.stopScan(leScanCallback)
            scanning = false
        }
        catch (e: SecurityException) {
            Log.e(TAG, "stopScan: $e")
            return false
        }
        return true
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(TAG, "onScanResult: ${result.device}")
            resultCallback?.let { it(result.device.address) }
        }
    }
}