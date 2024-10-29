package work.hirokuma.bleledcontrol.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import work.hirokuma.bleledcontrol.data.Device

private const val TAG = "BleScan"

class BleScan(private val context: Context) {
    private val bluetoothLeScanner: BluetoothLeScanner
    init {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    var scanning = false
        private set

    private var resultCallback: ((Device) -> Unit)? = null

    private var bleGatt: BluetoothGatt? = null

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
                        device = result.device,
                        scanRecord = record
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @SuppressLint("MissingPermission")
    fun connect(device: Device) {
        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.d(TAG, "onConnectionStateChange: status=$status, newState=$newState")
                if (gatt == null || status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "onConnectionStateChange: not success")
                    return
                }
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "onConnectionStateChange: connected!")
                        gatt.discoverServices()
                    }
                    BluetoothGatt.STATE_DISCONNECTED -> {
                        Log.d(TAG, "onConnectionStateChange: disconnected!")
                    }
                    else -> {
                        Log.e(TAG, "onConnectionStateChange: unknown state($newState)")
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                Log.d(TAG, "onServicesDiscovered: status=$status")
                if (gatt == null || status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "onServicesDiscovered: failed")
                    return
                }
                for (service in gatt.services) {
                    Log.d(TAG, "service: ${service.uuid}")
                    for (chas in service.characteristics) {
                        Log.d(TAG, "  characteristic: ${chas.uuid}")
                    }
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                Log.d(TAG, "onCharacteristicRead: deprecated: status=$status")
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, value, status)
                Log.d(TAG, "onCharacteristicRead: API33: status=$status, value=${value.toHexString()}")
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                Log.d(TAG, "onCharacteristicWrite: status=$status")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                Log.d(TAG, "onCharacteristicChanged: deprecated")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                super.onCharacteristicChanged(gatt, characteristic, value)
                Log.d(TAG, "onCharacteristicChanged: API33: value=${value.toHexString()}")
            }

            override fun onDescriptorRead(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int
            ) {
                super.onDescriptorRead(gatt, descriptor, status)
                Log.d(TAG, "onDescriptorRead: deprecated: status=$status")
            }

            override fun onDescriptorRead(
                gatt: BluetoothGatt,
                descriptor: BluetoothGattDescriptor,
                status: Int,
                value: ByteArray
            ) {
                super.onDescriptorRead(gatt, descriptor, status, value)
                Log.d(TAG, "onDescriptorRead: API33: status=$status, value=${value.toHexString()}")
            }

            override fun onDescriptorWrite(
                gatt: BluetoothGatt?,
                descriptor: BluetoothGattDescriptor?,
                status: Int
            ) {
                super.onDescriptorWrite(gatt, descriptor, status)
                Log.d(TAG, "onDescriptorWrite: status=$status")
            }

            override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
                super.onReliableWriteCompleted(gatt, status)
                Log.d(TAG, "onReliableWriteCompleted: status=$status")
            }

            override fun onServiceChanged(gatt: BluetoothGatt) {
                super.onServiceChanged(gatt)
                Log.d(TAG, "onServiceChanged")
            }

        }
        bleGatt = device.device?.connectGatt(context, false, callback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (bleGatt == null) {
            Log.w(TAG, "already disconnected")
            return
        }
        bleGatt!!.disconnect()
        bleGatt = null
    }
}