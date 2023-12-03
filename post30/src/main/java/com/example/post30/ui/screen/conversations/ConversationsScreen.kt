package com.example.post30.ui.screen.conversations

import androidx.compose.foundation.layout.Column
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

@Composable
fun ConversationsScreen(
    viewModel: ConversationsViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Conversations.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun ShowNotificationBlockPreview() {
    ShowNotificationBlock(response = null)
}