package work.hirokuma.bleledcontrol.data

import android.bluetooth.le.ScanRecord

data class Device(
    val name: String = "",
    val address: String = "",
    val ssid: Int = 0,
    val scanRecord: ScanRecord? = null,
)