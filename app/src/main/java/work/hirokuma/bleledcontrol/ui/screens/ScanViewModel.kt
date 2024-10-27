package work.hirokuma.bleledcontrol.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import work.hirokuma.bleledcontrol.data.LedControlRepository
import javax.inject.Inject

private const val TAG = "ScanViewModel"

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val controlRepository: LedControlRepository
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
        if (!controlRepository.searching) {
            _uiState.update {
                it.copy(scanning = true)
            }
            Log.d(TAG, "onClickScan: start searching")
            controlRepository.startDeviceSearch { device ->
                Log.d(TAG, "callback: $device.address")
                addDeviceAddress(device.address)
            }
        } else {
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: stop searching")
            controlRepository.stopDeviceSearch()
        }
    }
}
