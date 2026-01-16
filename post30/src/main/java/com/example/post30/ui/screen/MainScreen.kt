package com.example.post30.ui.screen

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
fun MainScreen(onScreenSelected: (Screen) -> Unit) {
    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Main.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    onScreenSelected(Screen.Privacy)
                }) {
                    Text(stringResource(R.string.label_privacy))
                }

                Button(onClick = {
                    onScreenSelected(Screen.NewFeatures)
                }) {
                    Text(stringResource(R.string.label_new_features))
                }
            }
        }
    )
}