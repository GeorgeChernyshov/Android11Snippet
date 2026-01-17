package com.example.post30.ui.screen.newfeatures.gwpasan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun GwpAsanScreen() {
    val helper = remember { TestGwpAsanHelper() }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.GwpAsan.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.performance_gwp_asan_hint))
                Text(stringResource(R.string.performance_gwp_asan_hint_2))
                Text(stringResource(R.string.performance_gwp_asan_hint_3))

                Button(onClick = {
                    helper.testGwpAsan()
                }) {
                    Text(stringResource(R.string.performance_gwp_asan_button))
                }
            }
        }
    )
}