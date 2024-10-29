package work.hirokuma.bleledcontrol.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    @SuppressLint("MissingPermission")
    fun getScanResults(): Flow<Device> = callbackFlow {
        if (scanning) {
            Log.e(TAG, "already scanning")
            return@callbackFlow
        }
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val record = result.scanRecord ?: return
                if (record.deviceName == null) {
                    return
                }
                trySend(Device(
                            address = result.device.address,
                            name = record.deviceName!!,
                            ssid = result.rssi,
                            scanRecord = record
                        )
                )
            }
        }

        try {
            Log.d(TAG, "scanLeDevice: startScan")
            bluetoothLeScanner.startScan(callback)
            scanning = true
        }
        catch (e: SecurityException) {
            Log.e(TAG, "startScan: $e")
            return@callbackFlow
        }

        awaitClose {
            bluetoothLeScanner.stopScan(callback)
            scanning = false
        }
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