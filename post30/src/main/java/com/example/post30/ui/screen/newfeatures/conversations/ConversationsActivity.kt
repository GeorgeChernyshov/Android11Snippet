package com.example.post30.ui.screen.newfeatures.conversations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.post30.ui.theme.Android11SnippetTheme

class ConversationsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Conversations() }
    }
}

@Composable
fun Conversations() {
    Android11SnippetTheme {
        ConversationsScreen()
    }
}