package work.hirokuma.bleledcontrol.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import work.hirokuma.bleledcontrol.data.Device
import work.hirokuma.bleledcontrol.data.LbsControlRepository
import javax.inject.Inject

private const val TAG = "ScanViewModel"

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val controlRepository: LbsControlRepository
): ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private fun addDevice(device: Device) {
        if (_uiState.value.deviceList.find { it.address == device.address } != null) {
            return
        }
        _uiState.update { state ->
            val newList = state.deviceList.toMutableList()
            newList.add(device)
            state.copy(
                deviceList = newList,
            )
        }
    }

    fun startDeviceScan() {
        if (!controlRepository.searching) {
            _uiState.update {
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
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: stop searching")
            controlRepository.stopDeviceSearch()
        }
    }

    fun connectDevice(device: Device) {
        controlRepository.connect(device)
        _uiState.update { state ->
            state.copy(
                selectedDevice = device,
            )
        }
        viewModelScope.launch {
            var onoff = true
            for (i in 1..50) {
                controlRepository.setLed(onoff)
                delay(500L)
                onoff = !onoff
            }
        }
    }

    fun disconnectDevice() {
        controlRepository.disconnect()
    }
}
