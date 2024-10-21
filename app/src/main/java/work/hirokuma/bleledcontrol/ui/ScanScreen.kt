package work.hirokuma.bleledcontrol.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import work.hirokuma.bleledcontrol.R
import work.hirokuma.bleledcontrol.ui.model.ScanViewModel
import work.hirokuma.bleledcontrol.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(onClick: () -> Unit, modifier: Modifier = Modifier, scanViewModel: ScanViewModel = ScanViewModel()) {
    val scanUiState by scanViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.app_name))
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                modifier = Modifier.clickable(onClick = onClick),
            ) {
                Text(
                    text = "scan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground,
        ) {
            DeviceList(
                addressList = scanUiState.addressList,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun DeviceList(
    addressList: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(addressList) { item ->
            Row(
                modifier = Modifier
                    .height(48.dp)
                    .border(width = 1.dp, color = colorScheme.primaryContainer)
                    .clickable { Log.d("row", "click: $item") }
            ) {
                Spacer(Modifier.width(16.dp))
                Text(
                    text = ":$item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.CenterVertically),
                    fontSize = 24.sp,
                )
            }
        }
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "light mode"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "dark mode"
)
@Composable
fun DeviceListPreview() {
    val viewModel = ScanViewModel()
    viewModel.addDeviceAddress("11:22:33:44:55:66")
    viewModel.addDeviceAddress("22:33:44:55:66:77")
    viewModel.addDeviceAddress("33:44:55:66:77:88")
    AppTheme {
        DeviceScreen({ Log.d("preview", "click") }, scanViewModel = viewModel)
    }
}
