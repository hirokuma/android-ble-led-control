package work.hirokuma.bleledcontrol.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

private const val TAG = "ScanViewModel"

class ScanViewModel: ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    var scanning: Boolean = false
        private set

    fun addDeviceAddress(address: String) {
        _uiState.update {
            val newList = it.addressList.toMutableList()
            newList.add(address)
            it.copy(
                addressList = newList,
            )
        }
    }
    
    fun onClickScan() {
        if (!scanning) {
            scanning = true
            Log.d(TAG, "onClickScan: true")
            viewModelScope.launch {
                while (scanning) {
                    delay(5000L)
                    val now = Date() // format(DateTimeFormatter.ISO_LOCAL_DATE)
                    addDeviceAddress("$now")
                }
            }
        } else {
            scanning = false
            Log.d(TAG, "onClickScan: false")
        }
    }
}