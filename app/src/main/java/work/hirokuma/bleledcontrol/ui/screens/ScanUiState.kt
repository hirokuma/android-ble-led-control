package work.hirokuma.bleledcontrol.ui.screens

import android.bluetooth.le.ScanRecord
import work.hirokuma.bleledcontrol.data.Device

data class ScanUiState(
    val deviceList: List<Device> = emptyList(),
    val scanning: Boolean = false,
    val selectedDevice: ScanRecord? = null,
)
