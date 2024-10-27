package work.hirokuma.bleledcontrol.ui.screens

data class ScanUiState(
    val addressList: List<String> = emptyList(),
    val scanning: Boolean = false,
)
