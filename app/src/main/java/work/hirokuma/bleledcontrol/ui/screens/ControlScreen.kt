package work.hirokuma.bleledcontrol.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import work.hirokuma.bleledcontrol.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    scanViewModel: ScanViewModel,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scanUiState by scanViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.primary,
                ),
                title = {
                    Text(scanUiState.selectedDevice?.name ?: "no name")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                modifier = Modifier.clickable(onClick = { onBackButtonClicked() }),
            ) {
                Text(
                    text = stringResource(R.string.back_button),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground,
        ) {
            Text("under construction")
        }
    }
}
