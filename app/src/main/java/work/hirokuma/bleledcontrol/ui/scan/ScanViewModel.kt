package work.hirokuma.bleledcontrol.ui.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import work.hirokuma.bleledcontrol.data.ble.BleScan
import javax.inject.Inject

private const val TAG = "ScanViewModel"

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val bleScan: BleScan
): ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private fun addDeviceAddress(address: String) {
        if (_uiState.value.addressList.find { it == address } != null) {
            return
        }
        _uiState.update { state ->
            val newList = state.addressList.toMutableList()
            newList.add(address)
            state.copy(
                addressList = newList,
            )
        }
    }
    
    fun onClickScan() {
        if (!bleScan.scanning) {
            _uiState.update {
                it.copy(scanning = true)
            }
            Log.d(TAG, "onClickScan: true")
            bleScan.startScan { address ->
                Log.d(TAG, "callback: $address")
                addDeviceAddress(address)
            }
        } else {
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: false")
            bleScan.stopScan()
        }
    }
}
