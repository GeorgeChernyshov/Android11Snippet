package com.example.post30

import android.app.AppOpsManager
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.post30.ui.AppViewModel
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.audit.AuditScreen
import com.example.post30.ui.screen.conversations.ConversationsActivity
import com.example.post30.ui.screen.conversations.ConversationsScreen
import com.example.post30.ui.screen.location.LocationScreen
import com.example.post30.ui.screen.media.MediaScreen
import com.example.post30.ui.screen.network.NetworkCapabilitiesScreen
import com.example.post30.ui.screen.packagevisibility.PackageVisibilityScreen
import com.example.post30.ui.screen.permissions.PermissionsScreen
import com.example.post30.ui.screen.storage.StorageScreen
import com.example.post30.ui.theme.Android11SnippetTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val appOpsCallback = object : AppOpsManager.OnOpNotedCallback() {

                override fun onNoted(syncNotedAppOp: SyncNotedAppOp) {
                    logPrivateDataAccess(
                        syncNotedAppOp.op,
                        Throwable().stackTrace.toString()
                    )
                }

                override fun onSelfNoted(syncNotedAppOp: SyncNotedAppOp) {
                    logPrivateDataAccess(
                        syncNotedAppOp.op,
                        Throwable().stackTrace.toString()
                    )
                }

                override fun onAsyncNoted(asyncNotedAppOp: AsyncNotedAppOp) {
                    logPrivateDataAccess(
                        asyncNotedAppOp.op,
                        asyncNotedAppOp.message
                    )
                }

                private fun logPrivateDataAccess(opCode: String, trace: String) {
                    Log.i(
                        APP_OP_TAG,
                        "Private data accessed. Operation: $opCode\nStack Trace:\n$trace"
                    )

                    viewModel.addAppOp(opCode)
                }
            }

            val appOpsManager = getSystemService(AppOpsManager::class.java)
            appOpsManager.setOnOpNotedCallback(mainExecutor, appOpsCallback)
        }

        setContent { App(viewModel) }
    }

    companion object {
        const val APP_OP_TAG = "AppOpCallback"
    }
}

@Composable
fun App(viewModel: AppViewModel) {
    Android11SnippetTheme {
        val context = LocalContext.current
        when (viewModel.currentScreen.value) {
            is Screen.Audit -> AuditScreen(
                appOps = viewModel.appOps.value,
                onNextClick = {
                    viewModel.setCurrentScreen(Screen.Media)
                }
            )

            is Screen.Conversations -> ConversationsScreen()

            is Screen.Location -> LocationScreen {
                viewModel.setCurrentScreen(Screen.Network)
            }

            is Screen.Media -> MediaScreen {
                context.startActivity(
                    Intent(context, ConversationsActivity::class.java)
                )
            }

            is Screen.Network -> NetworkCapabilitiesScreen {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    viewModel.setCurrentScreen(Screen.PackageVisibility)
                else viewModel.setCurrentScreen(Screen.Media)
            }

            is Screen.PackageVisibility -> PackageVisibilityScreen {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    viewModel.setCurrentScreen(Screen.Audit)
                else context.startActivity(
                    Intent(context, ConversationsActivity::class.java)
                )
            }

            is Screen.Permissions -> PermissionsScreen {
                viewModel.setCurrentScreen(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        Screen.Location
                    else Screen.Network
                )
            }

            is Screen.Storage -> StorageScreen {
                viewModel.setCurrentScreen(Screen.Permissions)
            }
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