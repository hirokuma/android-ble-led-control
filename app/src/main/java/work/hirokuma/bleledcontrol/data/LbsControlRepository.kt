package work.hirokuma.bleledcontrol.data

import android.util.Log
import kotlinx.coroutines.flow.StateFlow
import work.hirokuma.bleledcontrol.data.ble.LbsControl

private const val TAG = "LedControlRepository"

interface LbsControlRepository {
    val searching: Boolean
    val buttonState: StateFlow<Boolean>

    fun startDeviceSearch(callback: (Device) -> Unit)
    fun stopDeviceSearch()
    fun connect(device: Device)
    fun disconnect()
    fun setLed(onoff: Boolean)
}

class BleLbsControlRepository(
    private val lbsControl: LbsControl
): LbsControlRepository {
    override val searching: Boolean
        get() = lbsControl.scanning
    override val buttonState: StateFlow<Boolean>
        get() = lbsControl.buttonState

    override fun startDeviceSearch(callback: (Device) -> Unit) {
        lbsControl.startScan { device ->
            Log.d(TAG, "callback: $device")
            callback(device)
        }
    }

    override fun stopDeviceSearch() {
        lbsControl.stopScan()
    }

    override fun connect(device: Device) {
        Log.d(TAG, "connect: ${device.name}")
        lbsControl.connect(device)
    }

    override fun disconnect() {
        Log.d(TAG, "disconnect")
        lbsControl.disconnect()
    }

    override fun setLed(onoff: Boolean) {
        lbsControl.setLed(onoff)
    }
}

