package com.example.post30.ui.screen.conversations

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.security.SecurityActivity

@Composable
fun ConversationsScreen(
    viewModel: ConversationsViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Conversations.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        R.string.conversations_new
                    else R.string.conversations_old
                ))

                ShowNotificationBlock(response = state.value.response)
            }
        }
    )
}

@Composable
fun ShowNotificationBlock(
    modifier: Modifier = Modifier,
    response: String?
) {
    val context = LocalContext.current

    Column(modifier) {
        Button(onClick = { NotificationHelper(context).showChatNotification() }) {
            Text(text = stringResource(id = R.string.notification_send))
        }

        Text(text = response ?: stringResource(id = R.string.notification_reply))

        Button(
            onClick = {
                context.startActivity(
                    Intent(
                        context,
                        SecurityActivity::class.java
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.button_go_next))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowNotificationBlockPreview() {
    ShowNotificationBlock(response = null)
}