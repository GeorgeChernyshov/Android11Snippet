package com.example.post30.ui.screen.privacy

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun PrivacyScreen(onScreenSelected: (Screen) -> Unit) {
    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Privacy.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    onScreenSelected(Screen.Storage)
                }) {
                    Text(stringResource(R.string.label_storage))
                }

                Button(onClick = {
                    onScreenSelected(Screen.Permissions)
                }) {
                    Text(stringResource(R.string.label_permissions))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Button(onClick = {
                        onScreenSelected(Screen.Location)
                    }) {
                        Text(stringResource(R.string.label_location))
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Button(onClick = {
                        onScreenSelected(Screen.PackageVisibility)
                    }) {
                        Text(stringResource(R.string.label_package_visibility))
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Button(onClick = {
                        onScreenSelected(Screen.Audit)
                    }) {
                        Text(stringResource(R.string.label_audit))
                    }
                }
            }
        }
    )
}