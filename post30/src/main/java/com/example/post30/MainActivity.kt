package com.example.post30

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.ui.AppViewModel
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.conversations.ConversationsActivity
import com.example.post30.ui.screen.conversations.ConversationsScreen
import com.example.post30.ui.screen.location.LocationScreen
import com.example.post30.ui.screen.network.NetworkCapabilitiesScreen
import com.example.post30.ui.screen.packagevisibility.PackageVisibilityScreen
import com.example.post30.ui.screen.permissions.PermissionsScreen
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
        val context = LocalContext.current
        when (viewModel.currentScreen.value) {
            is Screen.Storage -> StorageScreen {
                viewModel.setCurrentScreen(Screen.Permissions)
            }

            is Screen.Permissions -> PermissionsScreen {
                viewModel.setCurrentScreen(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        Screen.Location
                    else Screen.Network
                )
            }

            is Screen.Location -> LocationScreen {
                viewModel.setCurrentScreen(Screen.Network)
            }

            is Screen.PackageVisibility -> PackageVisibilityScreen {
                context.startActivity(Intent(context, ConversationsActivity::class.java))
            }

            is Screen.Network -> NetworkCapabilitiesScreen {
                viewModel.setCurrentScreen(Screen.PackageVisibility)
            }

            is Screen.Conversations -> ConversationsScreen()
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