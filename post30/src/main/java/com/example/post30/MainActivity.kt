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
import com.example.post30.ui.screen.MainScreen
import com.example.post30.ui.screen.privacy.audit.AuditScreen
import com.example.post30.ui.screen.newfeatures.conversations.ConversationsActivity
import com.example.post30.ui.screen.newfeatures.conversations.ConversationsScreen
import com.example.post30.ui.screen.privacy.location.LocationScreen
import com.example.post30.ui.screen.newfeatures.media.MediaScreen
import com.example.post30.ui.screen.newfeatures.network.NetworkCapabilitiesScreen
import com.example.post30.ui.screen.newfeatures.NewFeaturesScreen
import com.example.post30.ui.screen.newfeatures.gwpasan.GwpAsanScreen
import com.example.post30.ui.screen.newfeatures.performance.PerformanceScreen
import com.example.post30.ui.screen.privacy.packagevisibility.PackageVisibilityScreen
import com.example.post30.ui.screen.privacy.permissions.PermissionsScreen
import com.example.post30.ui.screen.privacy.PrivacyScreen
import com.example.post30.ui.screen.privacy.storage.StorageScreen
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

    override fun onBackPressed() {
        if (!viewModel.navigateBack())
            finish()
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
            is Screen.Audit -> AuditScreen(viewModel.appOps.value)
            is Screen.Conversations -> ConversationsScreen()
            is Screen.GwpAsan -> GwpAsanScreen()
            is Screen.Location -> LocationScreen()
            is Screen.Main -> MainScreen(
                onScreenSelected = viewModel::navigateTo
            )

            is Screen.Media -> MediaScreen()
            is Screen.Network -> NetworkCapabilitiesScreen()
            is Screen.NewFeatures -> NewFeaturesScreen(
                onScreenSelected = viewModel::navigateTo
            )

            is Screen.PackageVisibility -> PackageVisibilityScreen()
            is Screen.Performance -> PerformanceScreen {
                viewModel.navigateTo(Screen.GwpAsan)
            }

            is Screen.Permissions -> PermissionsScreen()
            is Screen.Privacy -> PrivacyScreen(
                onScreenSelected = viewModel::navigateTo
            )

            is Screen.Security -> {}
            is Screen.Storage -> StorageScreen()
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