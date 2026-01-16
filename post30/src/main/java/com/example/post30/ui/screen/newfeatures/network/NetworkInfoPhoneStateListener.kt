package com.example.post30.ui.screen.newfeatures.network

import android.annotation.SuppressLint
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyDisplayInfo
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
class NetworkInfoPhoneStateListener(
    private val setConnectionStatus: (ConnectionStatus?) -> Unit
) : PhoneStateListener() {

    @SuppressLint("MissingPermission")
    override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
        super.onDisplayInfoChanged(telephonyDisplayInfo)
        val networkType = telephonyDisplayInfo.networkType
        val networkTypeOverride = telephonyDisplayInfo.overrideNetworkType

        val connectionStatus = ConnectionStatus.fromCellularNetworkType(
            networkType = networkType,
            networkTypeOverride = networkTypeOverride
        )

        setConnectionStatus(connectionStatus)
    }
}