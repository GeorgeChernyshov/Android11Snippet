package com.example.post30

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.ui.AppViewModel
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.storage.StorageScreen
import com.example.post30.ui.theme.Android11SnippetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App(
    viewModel: AppViewModel = viewModel()
) {
    Android11SnippetTheme {
        when (viewModel.currentScreen.value) {
            is Screen.Storage -> StorageScreen()
//            is Screen.Permissions ->
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android11SnippetTheme {
        Greeting("Android")
    }
}