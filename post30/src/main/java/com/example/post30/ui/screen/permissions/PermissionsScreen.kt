package com.example.post30.ui.screen.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = viewModel(),
    onNextClicked: () -> Unit
) {
    val context = LocalContext.current
    val telephonyManager = context.getSystemService(
        Context.TELEPHONY_SERVICE
    ) as TelephonyManager

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Permissions.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                RequestPermissionsBlock()
                ShowFloatingWindow(Modifier.padding(top = 16.dp))
                CheckPhoneNumberBlock(
                    modifier = Modifier.padding(top = 16.dp),
                    phoneNumber = viewModel.state.value.line1Number,
                    onClick = {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_PHONE_NUMBERS
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_PHONE_STATE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                                    Manifest.permission.READ_PHONE_NUMBERS
                                else Manifest.permission.READ_PHONE_STATE
                            )
                        } else {
                            viewModel.getPhoneNumber(telephonyManager.line1Number)
                        }
                    }
                )

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { onNextClicked.invoke() }
                ) {
                    Text(text = stringResource(id = R.string.button_go_next))
                }
            }
        }
    )
}

@Composable
fun RequestPermissionsBlock(modifier: Modifier = Modifier) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    Column(modifier) {
        Text(text = stringResource(id = R.string.permissions_one_time_hint))
        
        Button(onClick = {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }) {
            Text(text = stringResource(id = R.string.permissions_request_test))
        }
        
        Text(text = stringResource(id = R.string.permissions_request_multiple_times_hint))
    }
}

@Composable
fun ShowFloatingWindow(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val startActivityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(context)) {
            val window = FloatingWindow(context)
            window.open()
        }
    }

    Column(modifier) {
        Button(onClick = {
            if (Settings.canDrawOverlays(context)) {
                val window = FloatingWindow(context)
                window.open()
            } else {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                else Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.packageName)
                )

                startActivityLauncher.launch(intent)
            }
        }) {
            Text(text = stringResource(id = R.string.permissions_show_floating_window))
        }
    }
}

@Composable
fun CheckPhoneNumberBlock(
    modifier: Modifier = Modifier,
    phoneNumber: String?,
    onClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Button(onClick = onClick) {
            Text(text = stringResource(id = R.string.permissions_check_phone_number))
        }

        Text(text = stringResource(
            id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                R.string.permissions_read_phone_number_hint
            else R.string.permissions_read_phone_state_hint
        ))
        
        Text(text = phoneNumber.orEmpty())
    }
}

@Preview(showBackground = true)
@Composable
fun RequestPermissionsBlockPreview() {
    RequestPermissionsBlock()
}

@Preview(showBackground = true)
@Composable
fun ShowFloatingWindowBlockPreview() {
    ShowFloatingWindow()
}

@Preview(showBackground = true)
@Composable
fun CheckPhoneNumberBlockPreview() {
    CheckPhoneNumberBlock(phoneNumber = "+7 012 3456789")
}