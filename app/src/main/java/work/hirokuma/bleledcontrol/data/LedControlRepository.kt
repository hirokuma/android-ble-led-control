package work.hirokuma.bleledcontrol.data

import android.util.Log
import work.hirokuma.bleledcontrol.data.ble.BleScan

private const val TAG = "LedControlRepository"

interface LedControlRepository {
    val searching: Boolean
    fun startDeviceSearch(callback: (Device) -> Unit)
    fun stopDeviceSearch()
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
}
