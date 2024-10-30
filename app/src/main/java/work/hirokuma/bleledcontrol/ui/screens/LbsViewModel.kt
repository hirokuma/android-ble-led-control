package work.hirokuma.bleledcontrol.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import work.hirokuma.bleledcontrol.data.Device
import work.hirokuma.bleledcontrol.data.LbsControlRepository
import javax.inject.Inject

private const val TAG = "ScanViewModel"

@HiltViewModel
class LbsViewModel @Inject constructor(
    private val controlRepository: LbsControlRepository
): ViewModel() {
    // UI state
    private val _scanUiState = MutableStateFlow(ScanUiState())
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    private val _controlUiState = MutableStateFlow(ControlUiState())
    val controlUiState: StateFlow<ControlUiState> = _controlUiState.asStateFlow()

    private fun addDevice(device: Device) {
        if (_scanUiState.value.deviceList.find { it.address == device.address } != null) {
            return
        }
        _scanUiState.update { state ->
            val newList = state.deviceList.toMutableList()
            newList.add(device)
            state.copy(
                deviceList = newList,
            )
        }
    }

    fun startDeviceScan() {
        if (!controlRepository.searching) {
            _scanUiState.update {
                it.copy(
                    deviceList = emptyList(),
                    scanning = true
                )
            }
            Log.d(TAG, "onClickScan: start searching")
            controlRepository.startDeviceSearch { device ->
                Log.d(TAG, "callback: $device.address -- $device.name")
                addDevice(device)
            }
        } else {
            _scanUiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: stop searching")
            controlRepository.stopDeviceSearch()
        }
    }

    fun connectDevice(device: Device) {
        controlRepository.connect(device)
        _scanUiState.update { state ->
            state.copy(
                selectedDevice = device,
            )
        }
    }

    fun disconnectDevice() {
        controlRepository.disconnect()
    }

    fun setLed(onoff: Boolean) {
        controlRepository.setLed(onoff)
    }
}

data class ScanUiState(
    val deviceList: List<Device> = emptyList(),
    val scanning: Boolean = false,
    val selectedDevice: Device? = null,
)

data class ControlUiState(
    val buttonState: Boolean = false,
)