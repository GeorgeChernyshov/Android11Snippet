package com.example.post30.ui.screen.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun LocationScreen(
    viewModel: LocationViewModel = viewModel(),
    onNextClicked: () -> Unit
) {
    viewModel.updatePermissions(
        foregroundLocationPermitted = (ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED),
        backgroundLocationPermitted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            (ContextCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else false
    )

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Location.resourceId)) },
        content = {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text("Maybe make this screen work for Android below 10")
                }

                item {
                    Text(stringResource(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            R.string.location_simultaneous_request_new
                        else R.string.location_simultaneous_request_old
                    ))
                }

                item {
                    PermissionStatusBlock(
                        foregroundLocationPermitted = viewModel.state.value.foregroundLocationPermitted,
                        backgroundLocationPermitted = viewModel.state.value.backgroundLocationPermitted
                    )

                }

                item {
                    SimultaneousPermissionRequestBlock(
                        modifier = Modifier.padding(top = 16.dp),
                        onPermissionRequestResult = { foreground, background ->
                            viewModel.updatePermissions(foreground, background)
                        }
                    )
                }

                item {
                    SeparatePermissionRequestBlock(
                        modifier = Modifier.padding(top = 16.dp),
                        onForegroundPermissionResult = {
                            viewModel.updatePermissions(foregroundLocationPermitted = it)
                        },
                        onBackgroundPermissionResult = {
                            viewModel.updatePermissions(backgroundLocationPermitted = it)
                        }
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    item {
                        Text(stringResource(id = R.string.location_foreground_hint_1))
                    }

                    item {
                        Text(stringResource(id = R.string.location_foreground_hint_2))
                    }
                }

                item {
                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = { onNextClicked.invoke() }
                    ) {
                        Text(text = stringResource(id = R.string.button_go_next))
                    }
                }
            }
        }
    )
}

@Composable
fun PermissionStatusBlock(
    modifier: Modifier = Modifier,
    foregroundLocationPermitted: Boolean,
    backgroundLocationPermitted: Boolean
) {
    Column(modifier) {
        Text(text = stringResource(
            id = if (foregroundLocationPermitted)
                R.string.location_foreground_permitted
            else R.string.location_foreground_restricted
        ))

        Text(text = stringResource(
            id = if (backgroundLocationPermitted)
                R.string.location_background_permitted
            else R.string.location_background_restricted
        ))
    }
}

@Composable
fun SimultaneousPermissionRequestBlock(
    modifier: Modifier = Modifier,
    onPermissionRequestResult: (Boolean?, Boolean?) -> Unit = { _, _ -> }
) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onPermissionRequestResult.invoke(
                it[Manifest.permission.ACCESS_COARSE_LOCATION],
                it[Manifest.permission.ACCESS_BACKGROUND_LOCATION]
            )
        }
    }

    Column(modifier) {
        Text(text = stringResource(id = R.string.location_simultaneous_request_hint))
        
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ))
            }
        }) {
            Text(text = stringResource(id = R.string.location_request))
        }
    }
}

@Composable
fun SeparatePermissionRequestBlock(
    modifier: Modifier = Modifier,
    onForegroundPermissionResult: (Boolean) -> Unit = { _ -> },
    onBackgroundPermissionResult: (Boolean) -> Unit = { _ -> },
) {
    val context = LocalContext.current

    val requestBackgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onBackgroundPermissionResult.invoke(it)
    }

    val requestForegroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onForegroundPermissionResult.invoke(isGranted)

        if (isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestBackgroundPermissionLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    Column(modifier) {
        Text(text = stringResource(id = R.string.location_separate_request_hint))

        Text(stringResource(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                R.string.location_background_new
            else R.string.location_background_old
        ))

        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val foreground = (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)

                val background = (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)

                when {
                    !foreground -> requestForegroundPermissionLauncher
                        .launch(Manifest.permission.ACCESS_COARSE_LOCATION)

                    !background -> requestBackgroundPermissionLauncher
                        .launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            }
        }) {
            Text(text = stringResource(id = R.string.location_request))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionStatusBlockPreview() {
    PermissionStatusBlock(
        foregroundLocationPermitted = false,
        backgroundLocationPermitted = false
    )
}

@Preview(showBackground = true)
@Composable
fun SimultaneousPermissionRequestBlockPreview() {
    SimultaneousPermissionRequestBlock()
}

@Preview(showBackground = true)
@Composable
fun SeparatePermissionRequestBlockPreview() {
    SeparatePermissionRequestBlock()
}