package work.hirokuma.bleledcontrol.data

import android.util.Log
import work.hirokuma.bleledcontrol.data.ble.BleScan

private const val TAG = "LedControlRepository"

interface LedControlRepository {
    val searching: Boolean
    fun startDeviceSearch(callback: (Device) -> Unit)
    fun stopDeviceSearch()
    fun connect(device: Device)
    fun disconnect()
    fun setLed(onoff: Boolean)
}

class BleLedControlRepository(
    private val bleScan: BleScan
): LedControlRepository {
    override val searching: Boolean
        get() = bleScan.scanning

    override fun startDeviceSearch(callback: (Device) -> Unit) {
        bleScan.startScan { device ->
            Log.d(TAG, "callback: $device")
            callback(device)
        }
    }

    override fun stopDeviceSearch() {
        bleScan.stopScan()
    }

    override fun connect(device: Device) {
        Log.d(TAG, "connect: ${device.name}")
        bleScan.connect(device)
    }

    override fun disconnect() {
        Log.d(TAG, "disconnect")
        bleScan.disconnect()
    }

    override fun setLed(onoff: Boolean) {
        bleScan.setLed(onoff)
    }
}
