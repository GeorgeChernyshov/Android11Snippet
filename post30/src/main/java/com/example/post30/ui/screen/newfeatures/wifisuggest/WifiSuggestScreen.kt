package com.example.post30.ui.screen.newfeatures.wifisuggest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiEnterpriseConfig.Eap
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.hotspot2.pps.Credential
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.Config
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WifiSuggestScreen() {
    var connected by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val wifiManager = remember {
        context.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
    }

    val localSuggestion = remember {
        WifiNetworkSuggestion.Builder()
            .setSsid(Config.WIFI_SSID)
            .setWpa2Passphrase(Config.WIFI_PASSWORD)
            .build()
    }

    val passpointSuggestion = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WifiNetworkSuggestion.Builder()
                .setPasspointConfig(
                    PasspointConfiguration().apply {
                        homeSp = HomeSp().apply {
                            fqdn = DUMMY_PASSPOINT_FQDN
                            friendlyName = DUMMY_PASSPOINT_REALM
                        }

                        credential = Credential().apply {
                            realm = DUMMY_PASSPOINT_REALM
                            userCredential = Credential.UserCredential().apply {
                                eapType = 21
                                nonEapInnerMethod = "PAP"
//                                type = Credential.UserCredential.CREDENTIAL_TYPE_USERNAME_PASSWORD
                                username = DUMMY_PASSPOINT_USERNAME
                                password = DUMMY_PASSPOINT_PASSWORD
                            }
                        }
                    }
                )
                .build()
        } else null
    }

    DisposableEffect(Unit) {


        val intentFilter = IntentFilter(
            WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION
        )

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                connected =  intent.action
                    .equals(
                        WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION
                    )
            }
        }

        val status = wifiManager.addNetworkSuggestions(
            listOfNotNull(localSuggestion, passpointSuggestion)
        )

        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            context.registerReceiver(
                broadcastReceiver,
                intentFilter
            )
        }

        onDispose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.WifiSuggest.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.wifi_suggest_title))
                Text(stringResource(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        R.string.wifi_suggest_hint_new
                    else R.string.wifi_suggest_hint_old
                ))

                if (connected)
                    Text(stringResource(R.string.wifi_suggest_connected))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Text(stringResource(R.string.wifi_suggest_hint_passpoint))
                    Text(stringResource(R.string.wifi_suggest_remove_hint))

                    Button(
                        onClick = {
                            wifiManager.removeNetworkSuggestions(
                                listOf(localSuggestion)
                            )
                        }
                    ) {
                        Text(stringResource(R.string.wifi_suggest_remove_button))
                    }
                }
            }
        }
    )
}

private const val DUMMY_PASSPOINT_FQDN = "test.passpoint.example.com"
private const val DUMMY_PASSPOINT_REALM = "test.passpoint.example.com" // Often same as FQDN
private const val DUMMY_PASSPOINT_USERNAME = "passpoint_user"
private const val DUMMY_PASSPOINT_PASSWORD = "passpoint_password"