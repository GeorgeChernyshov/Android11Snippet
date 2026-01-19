package com.example.post30.ui.screen.newfeatures

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.example.post30.ui.screen.newfeatures.conversations.ConversationsActivity
import com.example.post30.ui.screen.newfeatures.security.SecurityActivity

@Composable
fun NewFeaturesScreen(onScreenSelected: (Screen) -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.NewFeatures.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    onScreenSelected(Screen.Media)
                }) {
                    Text(stringResource(R.string.label_media))
                }

                Button(onClick = {
                    onScreenSelected(Screen.Network)
                }) {
                    Text(stringResource(R.string.label_network))
                }

                Button(
                    onClick = { context.startActivity(
                        Intent(context, ConversationsActivity::class.java)
                    ) }
                ) {
                    Text(stringResource(R.string.label_conversations))
                }

                Button(
                    onClick = { context.startActivity(
                        Intent(context, SecurityActivity::class.java)
                    ) }
                ) {
                    Text(stringResource(R.string.label_security))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Button(onClick = {
                        onScreenSelected(Screen.Performance)
                    }) {
                        Text(stringResource(R.string.label_performance))
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Button(onClick = {
                        onScreenSelected(Screen.Text)
                    }) {
                        Text(stringResource(R.string.label_text))
                    }
                }
            }
        }
    )
}