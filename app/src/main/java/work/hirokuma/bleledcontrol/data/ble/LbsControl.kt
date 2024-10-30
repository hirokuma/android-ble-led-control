package work.hirokuma.bleledcontrol.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import work.hirokuma.bleledcontrol.data.Device
import java.lang.Thread.State
import java.util.UUID

private const val TAG = "LbsControl"

class LbsControl(private val context: Context) {
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
    private var ledCharacteristic: BluetoothGattCharacteristic? = null
    private var buttonCharacteristic: BluetoothGattCharacteristic? = null
    private val _buttonState = MutableStateFlow(false)
    val buttonState: StateFlow<Boolean> = _buttonState.asStateFlow()

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
                val service = gatt.getService(BLINKY_SERVICE_UUID)
                if (service == null) {
                    Log.e(TAG, "onServicesDiscovered: service not exist")
                    return
                }
                val ledChas = service.getCharacteristic(BLINKY_LED_CHARACTERISTIC_UUID)
                if (ledChas == null) {
                    Log.e(TAG, "onServicesDiscovered: LED characteristic not exist")
                    return
                }
                val buttonChas = service.getCharacteristic(BLINKY_BUTTON_CHARACTERISTIC_UUID)
                if (buttonChas == null) {
                    Log.e(TAG, "onServicesDiscovered: Button characteristic not exist")
                    return
                }
                // 保持するようなものではない？
                ledCharacteristic = ledChas
                buttonCharacteristic = buttonChas

                // ここで read すると次の write が動作せず notify されなくなる
                // gatt.readCharacteristic(buttonCharacteristic)

                // Notify有効
                gatt.setCharacteristicNotification(buttonCharacteristic, true)
                val descriptor = buttonCharacteristic!!.getDescriptor(CCCD_UUID)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.d(TAG, "writeDescriptor(API33)")
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    Log.d(TAG, "writeDescriptor(deprecated)")
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }

                Log.d(TAG, "onServicesDiscovered: done")
            }

            @Deprecated("Deprecated in Java")
            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
                characteristic?.let {
                    Log.d(TAG, "onCharacteristicRead: deprecated: status=$status, uuid=${it.uuid}, value=${it.value.toHexString()}")
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, value, status)
                Log.d(TAG, "onCharacteristicRead: API33: status=$status, uuid=${characteristic.uuid}, value=${value.toHexString()}")
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                characteristic?.let {
                    Log.d(TAG, "onCharacteristicWrite: status=$status, uuid=${it.uuid}")
                    if (it.uuid == BLINKY_BUTTON_CHARACTERISTIC_UUID) {
                        Log.d(TAG, "button")
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                characteristic?.let {
                    Log.d(TAG, "onCharacteristicChanged: deprecated: uuid=${it.uuid}, value=${it.value.toHexString()}")
                    if (it.uuid == BLINKY_BUTTON_CHARACTERISTIC_UUID) {
                        Log.d(TAG, "button")
                        _buttonState.update { _ ->
                            it.value[0].toInt() != 0x00
                        }
                    }
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray
            ) {
                super.onCharacteristicChanged(gatt, characteristic, value)
                Log.d(TAG, "onCharacteristicChanged: API33: value=${value.toHexString()}")
                if (characteristic.uuid == BLINKY_BUTTON_CHARACTERISTIC_UUID) {
                    Log.d(TAG, "button")
                    _buttonState.update { _ ->
                        value[0].toInt() != 0x00
                    }
                }
            }

            @Deprecated("Deprecated in Java")
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
    fun setLed(onoff: Boolean) {
        if (bleGatt == null || ledCharacteristic == null) {
            Log.e(TAG, "setLed: characteristic is null")
            return
        }
        val data = byteArrayOf(if (onoff) 1 else 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "setLed: writeCharacteristic(API33): value=${onoff}")
            bleGatt!!.writeCharacteristic(ledCharacteristic!!, data, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        } else {
            Log.d(TAG, "setLed: writeCharacteristic(deprecated): value=${onoff}")
            ledCharacteristic!!.setValue(data)
            bleGatt!!.writeCharacteristic(ledCharacteristic!!)
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (bleGatt == null) {
            Log.w(TAG, "already disconnected")
            return
        }
        bleGatt!!.disconnect()
        ledCharacteristic = null
        buttonCharacteristic = null
        bleGatt = null
    }

    // https://docs.nordicsemi.com/bundle/ncs-latest/page/nrf/libraries/bluetooth_services/services/lbs.html
    // https://github.com/NordicSemiconductor/Android-nRF-Blinky/blob/506cabe8884364cd4302cc490664ec020c42728b/blinky/spec/src/main/java/no/nordicsemi/android/blinky/spec/BlinkySpec.kt#L10
    companion object {
        val BLINKY_SERVICE_UUID: UUID = UUID.fromString("00001523-1212-efde-1523-785feabcd123")
        val BLINKY_BUTTON_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001524-1212-efde-1523-785feabcd123")
        val BLINKY_LED_CHARACTERISTIC_UUID: UUID = UUID.fromString("00001525-1212-efde-1523-785feabcd123")

        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
    }
}
