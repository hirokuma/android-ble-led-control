package work.hirokuma.bleledcontrol.ui.model

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import work.hirokuma.bleledcontrol.data.BleScan
import java.util.Date
import javax.inject.Inject

private const val TAG = "ScanViewModel"

//@HiltViewModel
//class ScanViewModel @Inject constructor(val context: Context): ViewModel() {
//    private val bluetoothLeScanner: BluetoothLeScanner
//    init {
//        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter = bluetoothManager.adapter
//        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
//    }

@HiltViewModel(assistedFactory = ScanViewModelFactory::class)
class ScanViewModel @AssistedInject constructor(
    @Assisted val counter: Int
): ViewModel() {
    init {
        Log.d(TAG, "init: $counter")
    }

    // UI state
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

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
        if (!uiState.value.scanning) {
            _uiState.update {
                it.copy(scanning = true)
            }
            Log.d(TAG, "onClickScan: true")
            viewModelScope.launch {
                while (uiState.value.scanning) {
                    delay(5000L)
                    val now = Date() // format(DateTimeFormatter.ISO_LOCAL_DATE)
                    addDeviceAddress("$now")
                }
            }
        } else {
            _uiState.update {
                it.copy(scanning = false)
            }
            Log.d(TAG, "onClickScan: false")
        }
    }

}

@AssistedFactory
interface ScanViewModelFactory {
    fun create(counter: Int) : ScanViewModel
}
