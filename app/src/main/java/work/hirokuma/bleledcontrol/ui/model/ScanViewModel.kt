package work.hirokuma.bleledcontrol.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScanViewModel: ViewModel() {

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
}