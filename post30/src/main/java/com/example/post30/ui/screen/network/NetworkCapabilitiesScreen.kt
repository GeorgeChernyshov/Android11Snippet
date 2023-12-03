package com.example.post30.ui.screen.network

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED
import android.net.NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO
import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA
import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE
import android.telephony.TelephonyManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun NetworkCapabilitiesScreen(
    viewModel: NetworkViewModel = viewModel(),
    onNextClicked: () -> Unit
) {
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)

            val meteredness = when {
                networkCapabilities.hasCapability(NET_CAPABILITY_NOT_METERED) -> NetworkScreenState.Meteredness.UNMETERED

                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                        networkCapabilities.hasCapability(NET_CAPABILITY_TEMPORARILY_NOT_METERED)) -> {
                    NetworkScreenState.Meteredness.TEMPORARILY_UNMETERED
                }

                else -> NetworkScreenState.Meteredness.METERED
            }

            viewModel.setMeteredness(meteredness)
            viewModel.setBandwidth(
                networkCapabilities.linkDownstreamBandwidthKbps,
                networkCapabilities.linkUpstreamBandwidthKbps
            )
        }
    })

    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Network.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                CheckMeterednessBlock(meteredness = state.value.meteredness)

                FiveGStatusBlock(
                    modifier = Modifier.padding(top = 16.dp),
                    fiveGStatus = state.value.fiveGStatus
                ) { viewModel.setFiveGStatus(it) }

                BandwidthEstimationBlock(
                    modifier = Modifier.padding(top = 16.dp),
                    downstreamBandwidth = state.value.downstreamBandwidth,
                    upstreamBandwidth = state.value.upstreamBandwidth
                )

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { onNextClicked.invoke() }
                ) {
                    Text(text = stringResource(id = R.string.button_go_next))
                }
            }
        }
    )
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
    fiveGStatus: Int?,
    setFiveGStatus: (Int?) -> Unit = {}
) {
    val context = LocalContext.current
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) listenForNetworkType(context, setFiveGStatus)
    }

    val stringRes = when (fiveGStatus) {
        OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> R.string.network_five_g_lte_advanced_pro
        OVERRIDE_NETWORK_TYPE_NR_NSA -> R.string.network_five_g_nr_nsa
        OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> R.string.network_five_g_nr_nsa_mmwave
        null -> R.string.network_five_g_not_checked
        else -> R.string.network_five_g_not
    }

    Column(modifier = modifier) {
        Button(onClick = {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                listenForNetworkType(context, setFiveGStatus)
            } else requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }) {
            Text(text = stringResource(id = R.string.network_five_g_check))
        }

        Text(text = stringResource(id = stringRes))
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
fun CheckMeterednessBlockPreview() {
    CheckMeterednessBlock(meteredness = NetworkScreenState.Meteredness.UNMETERED)
}

@Preview(showBackground = true)
@Composable
fun FiveGStatusBlockPreview() {
    FiveGStatusBlock(fiveGStatus = null)
}

@Preview(showBackground = true)
@Composable
fun BandwidthEstimationBlockPreview() {
    BandwidthEstimationBlock(
        downstreamBandwidth = null,
        upstreamBandwidth = null
    )
}

fun listenForNetworkType(
    context: Context,
    setFiveGStatus: (Int?) -> Unit = {}
) {
    val telephonyManager = context.getSystemService(
        Context.TELEPHONY_SERVICE
    ) as TelephonyManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val phoneStateListener = object : PhoneStateListener() {
            override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) return

                super.onDisplayInfoChanged(telephonyDisplayInfo)
                setFiveGStatus(telephonyDisplayInfo.overrideNetworkType)
            }
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED)
    } else setFiveGStatus(null)
}