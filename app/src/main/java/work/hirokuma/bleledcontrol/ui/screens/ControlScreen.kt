package work.hirokuma.bleledcontrol.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import work.hirokuma.bleledcontrol.R
import work.hirokuma.bleledcontrol.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
    lbsViewModel: LbsViewModel,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scanUiState by lbsViewModel.scanUiState.collectAsState()
    val buttonState by lbsViewModel.buttonState.collectAsState()

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
            val resId = if (buttonState) R.string.button_on_state else R.string.button_off_state
            Control(
                stringResource(resId),
                onSetButtonClicked = { lbsViewModel.setLed(true) },
                onUnsetButtonClicked = { lbsViewModel.setLed(false) }
            )
        }
    }
}

@Composable
fun Control(
    buttonStateText: String,
    onSetButtonClicked: () -> Unit = {},
    onUnsetButtonClicked: () -> Unit = {},
) {
    Column(modifier = Modifier) {
        Text(
            text = stringResource(R.string.led_control_row),
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Button(
                onClick = onSetButtonClicked
            ) {
                Text(stringResource(R.string.led_on_button))
            }
            Button(
                onClick = onUnsetButtonClicked
            ) {
                Text(stringResource(R.string.led_off_button))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.button_state_row),
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primaryContainer)
        )
        Text(
            text = buttonStateText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
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
fun ControlScreenPreview() {
    AppTheme(dynamicColor = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background,
            contentColor = colorScheme.onBackground,
        ) {
            Control("test")
        }
    }
}
