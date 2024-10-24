package work.hirokuma.bleledcontrol.data

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

private const val TAG = "BleScan"

interface BleScan {
    val scanning: Boolean

    fun scanLeDevice()
}

class DefaultBleScan(context: Context): BleScan {
    private val bluetoothLeScanner: BluetoothLeScanner
    init {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    override var scanning = false
        private set

    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 10 seconds.
    private val scanPeriod: Long = 10000

    override fun scanLeDevice() {
        try {
            if (!scanning) { // Stops scanning after a pre-defined scan period.
                Log.d(TAG, "scanLeDevice: !scanning")
                handler.postDelayed({
                    scanning = true
                    Log.d(TAG, "scanLeDevice: startScan")
                    bluetoothLeScanner.startScan(leScanCallback)
                }, scanPeriod)
            } else {
                scanning = false
                Log.d(TAG, "scanLeDevice: stopScan 2")
                bluetoothLeScanner.stopScan(leScanCallback)
            }
        }
        catch (e: SecurityException) {
            Log.e(TAG, "")
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
}
