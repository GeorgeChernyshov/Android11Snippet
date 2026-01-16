package com.example.post30.ui.screen.privacy.audit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun AuditScreen(appOps: List<String>) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLocation(locationManager)
        } else {
            Log.d("AuditScreen", "Location permission denied.")
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Audit.resourceId))
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(stringResource(R.string.audit_hint))
                }

                item {
                    Text(stringResource(R.string.audit_request_location_hint))
                }

                item {
                    Button(
                        onClick = {
                            tryGetLocation(
                                context = context,
                                locationManager = locationManager,
                                locationPermissionLauncher = locationPermissionLauncher
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.audit_request_location))
                    }
                }

                item {
                    Text(stringResource(R.string.audit_log_hint))
                }

                items(appOps.size) { index ->
                    Text(appOps[index])
                }
            }
        }
    )
}

fun tryGetLocation(
    context: Context,
    locationManager: LocationManager,
    locationPermissionLauncher: ActivityResultLauncher<String>
) {
    val permissionGranted = context.checkSelfPermission(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (permissionGranted)
        getLocation(locationManager)
    else locationPermissionLauncher
        .launch(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun getLocation(locationManager: LocationManager) {
    try {
        val lastLocation = locationManager
            .getLastKnownLocation(LocationManager.GPS_PROVIDER)

        Log.d("AuditScreen", "Last known location: $lastLocation")
    } catch (e: SecurityException) {
        Log.e(
            "AuditScreen",
            "SecurityException: Location permission denied (already granted but still an issue).",
            e
        )
    }
}