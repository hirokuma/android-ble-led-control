package work.hirokuma.bleledcontrol.ui.scan

data class ScanUiState(
    val addressList: List<String> = emptyList(),
    val scanning: Boolean = false,
)
