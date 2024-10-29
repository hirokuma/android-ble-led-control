package work.hirokuma.bleledcontrol.data.ble

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import work.hirokuma.bleledcontrol.data.Device

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

    private var resultCallback: ((Device) -> Unit)? = null

    fun startScan(resultCallback: (Device) -> Unit): Boolean {
        if (scanning) {
            Log.d(TAG, "already scanning")
            return false
        }
        try {
            Log.d(TAG, "scanLeDevice: startScan")
            bluetoothLeScanner.startScan(scanCallback)
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
            bluetoothLeScanner.stopScan(scanCallback)
            scanning = false
            resultCallback = null
        }
        catch (e: SecurityException) {
            Log.e(TAG, "stopScan: $e")
            return false
        }
        return true
    }

    // Device scan callback.
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(TAG, "onScanResult: ${result.device}")
            val record = result.scanRecord ?: return
            Log.d(TAG, "ScanRecord: $result.scanRecord}")
            if (record.deviceName == null) {
                return
            }
            resultCallback?.let {
                it(
                    Device(
                        address = result.device.address,
                        name = record.deviceName!!,
                        ssid = result.rssi,
                        scanRecord = record
                    )
                )
            }
        }
    }
}