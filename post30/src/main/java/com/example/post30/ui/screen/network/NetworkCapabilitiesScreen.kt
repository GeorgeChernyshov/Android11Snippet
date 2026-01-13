package com.example.post30.ui.screen.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen
import java.net.NetworkInterface
import java.net.SocketException
import kotlin.text.append
import kotlin.text.format
import kotlin.text.indices

@Composable
fun NetworkCapabilitiesScreen(
    viewModel: NetworkViewModel = viewModel(),
    onNextClicked: () -> Unit
) {
    val context = LocalContext.current
    val connectivityManager = remember {
        context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }

    val telephonyManager = remember {
        context.getSystemService(
            Context.TELEPHONY_SERVICE
        ) as TelephonyManager
    }

    val wifiManager = remember {
        context.getSystemService(
            Context.WIFI_SERVICE
        ) as WifiManager
    }

    val networkCallback = remember {
        NetworkCallback(
            setMeteredness = viewModel::setMeteredness,
            setBandwidth = viewModel::setBandwidth
        )
    }

    val phoneStateListener = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            NetworkInfoPhoneStateListener(viewModel::setCellularConnectionStatus)
        else null
    }

    DisposableEffect(Unit) {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Network.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
//                MacAddressBlock()
                CheckMeterednessBlock(meteredness = state.value.meteredness)

                FiveGStatusBlock(
                    modifier = Modifier.padding(top = 16.dp),
                    connectionStatus = state.value.connectionStatus,
                    listenForNetworkType = {
                        listenForNetworkType(
                            connectivityManager = connectivityManager,
                            wifiManager = wifiManager,
                            telephonyManager = telephonyManager,
                            phoneStateListener = phoneStateListener,
                            setConnectionType = viewModel::setConnectionType,
                            setWiFiConnectionStatus = viewModel::setWiFiConnectionStatus,
                            setCellularConnectionStatus = viewModel::setCellularConnectionStatus
                        )
                    }
                )

                BandwidthEstimationBlock(
                    modifier = Modifier.padding(top = 16.dp),
                    downstreamBandwidth = state.value.downstreamBandwidth,
                    upstreamBandwidth = state.value.upstreamBandwidth
                )

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = onNextClicked
                ) {
                    Text(text = stringResource(id = R.string.button_go_next))
                }
            }
        }
    )
}

@Composable
fun MacAddressBlock() {
    Text(getMacAddressInformation())
}

@Composable
fun CheckMeterednessBlock(
    modifier: Modifier = Modifier,
    meteredness: NetworkScreenState.Meteredness?
) {
    val stringRes = when (meteredness) {
        NetworkScreenState.Meteredness.UNMETERED -> R.string.network_meteredness_unmetered

        NetworkScreenState.Meteredness.TEMPORARILY_UNMETERED ->
            R.string.network_meteredness_temp_unmetered

        NetworkScreenState.Meteredness.METERED -> R.string.network_meteredness_metered

        else -> R.string.network_meteredness_unknown
    }

    Text(
        modifier = modifier,
        text = stringResource(id = stringRes)
    )
}

@Composable
fun FiveGStatusBlock(
    modifier: Modifier = Modifier,
    connectionStatus: ConnectionStatus?,
    listenForNetworkType: () -> Unit = {}
) {
    val context = LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) listenForNetworkType()
    }

    Column(modifier = modifier) {
        Button(onClick = {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                listenForNetworkType()
            } else requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }) {
            Text(text = stringResource(id = R.string.network_five_g_check))
        }

        connectionStatus?.let {
            Text(stringResource(it.stringRes))
        }
    }
}

@Composable
fun BandwidthEstimationBlock(
    modifier: Modifier = Modifier,
    downstreamBandwidth: Int?,
    upstreamBandwidth: Int?
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.network_bandwidth_downstream))
            Text(
                text = downstreamBandwidth?.toString()
                    ?: stringResource(id = R.string.network_bandwidth_unknown)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.network_bandwidth_upstream))
            Text(
                text = upstreamBandwidth?.toString()
                    ?: stringResource(id = R.string.network_bandwidth_unknown)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MacAddressBlockPreview() {
    MacAddressBlock()
}

@Preview(showBackground = true)
@Composable
fun CheckMeterednessBlockPreview() {
    CheckMeterednessBlock(meteredness = NetworkScreenState.Meteredness.UNMETERED)
}

@Preview(showBackground = true)
@Composable
fun FiveGStatusBlockPreview() {
    FiveGStatusBlock(connectionStatus = null)
}

@Preview(showBackground = true)
@Composable
fun BandwidthEstimationBlockPreview() {
    BandwidthEstimationBlock(
        downstreamBandwidth = null,
        upstreamBandwidth = null
    )
}

@SuppressLint("MissingPermission")
fun listenForNetworkType(
    connectivityManager: ConnectivityManager,
    wifiManager: WifiManager,
    telephonyManager: TelephonyManager,
    phoneStateListener: PhoneStateListener?,
    setConnectionType: (ConnectionType) -> Unit,
    setWiFiConnectionStatus: (ConnectionStatus?) -> Unit,
    setCellularConnectionStatus: (ConnectionStatus?) -> Unit
) {
    val activeNetwork = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager
        .getNetworkCapabilities(activeNetwork)

    when {
        networkCapabilities == null -> {
            setConnectionType(ConnectionType.NO_CONNECTION)
            setCellularConnectionStatus(ConnectionStatus.NO_CONNECTION)
        }

        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
            setConnectionType(ConnectionType.WIFI)
            setWiFiConnectionStatus(
                getWifiConnectionStatus(networkCapabilities, wifiManager)
            )
        }

        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
            setConnectionType(ConnectionType.CELLULAR)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                listenForCellularNetworkType(
                    telephonyManager = telephonyManager,
                    phoneStateListener = phoneStateListener
                )
            } else {
                val connectionStatus = ConnectionStatus.fromCellularNetworkType(
                    networkType = telephonyManager.networkType,
                    networkTypeOverride = 0
                )

                setCellularConnectionStatus(connectionStatus)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun listenForCellularNetworkType(
    telephonyManager: TelephonyManager,
    phoneStateListener: PhoneStateListener?,
) {
    telephonyManager.listen(
        phoneStateListener,
        PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED
    )
}

fun getWifiConnectionStatus(
    networkCapabilities: NetworkCapabilities,
    wifiManager: WifiManager
): ConnectionStatus {
    val wifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        networkCapabilities.transportInfo as? WifiInfo
            ?: wifiManager.connectionInfo
    else wifiManager.connectionInfo

    return wifiInfo?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (it.wifiStandard) {
                ScanResult.WIFI_STANDARD_11AX -> ConnectionStatus.WIFI_6
                ScanResult.WIFI_STANDARD_11AC -> ConnectionStatus.WIFI_5
                ScanResult.WIFI_STANDARD_11N -> ConnectionStatus.WIFI_4

                else -> ConnectionStatus.WIFI_OTHER
            }
        } else {
            when {
                it.frequency >= 5000 -> ConnectionStatus.WIFI_5
                it.frequency >= 2400 -> ConnectionStatus.WIFI_4

                else -> ConnectionStatus.WIFI_OTHER
            }
        }
    } ?: ConnectionStatus.WIFI_OTHER
}

// TODO finish this once I get an actual device
fun getMacAddressInformation(): String {
    val stringBuilder = StringBuilder()

    try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val intf: NetworkInterface = interfaces.nextElement()

            if (intf.isLoopback || !intf.isUp) {
                // Ignore loopback and down interfaces
                continue
            }

            val mac = intf.hardwareAddress
            if (mac == null || mac.isEmpty()) {
                stringBuilder.appendLine("${intf.displayName}, ${intf.name}")
                continue
            }

            val buf = StringBuilder()
            for (i in mac.indices) {
                buf.append(String.format("%02X%s", mac[i], if (i < mac.size - 1) ":" else ""))
            }
            stringBuilder.append(
                stringBuilder.appendLine("${intf.displayName}, ${intf.name}, $buf")
            )
        }
    } catch (ex: SocketException) {
        Log.e("NetworkCapabilitiesScreen", ex.toString())
    }

    return stringBuilder.toString()
}