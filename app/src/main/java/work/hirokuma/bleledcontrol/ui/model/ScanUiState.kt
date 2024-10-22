package work.hirokuma.bleledcontrol.ui.model

data class ScanUiState(
    val addressList: List<String> = emptyList(),
    val scanning: Boolean = false,
)
