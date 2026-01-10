package com.example.post30.ui.screen.packagevisibility

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen
import kotlin.collections.emptyList

@Composable
fun PackageVisibilityScreen(onNextClick: () -> Unit) {

    val packages: MutableState<List<String>> = remember {
        mutableStateOf(emptyList())
    }

    val packageManager = LocalContext.current.packageManager

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.PackageVisibility.resourceId))
        },
        bottomBar = {
            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.button_go_next))
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.package_visibility_hint))

                Button(
                    onClick = {
                        packages.value = packageManager.queryIntentActivities(
                            Intent(Intent.ACTION_MAIN),
                            PackageManager.MATCH_ALL
                        ).map { it.activityInfo.packageName }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.package_visibility_query_all))
                }

                LazyColumn {
                    items(packages.value.size) { index ->
                        Text(packages.value[index])
                    }
                }
            }
        }
    )
}