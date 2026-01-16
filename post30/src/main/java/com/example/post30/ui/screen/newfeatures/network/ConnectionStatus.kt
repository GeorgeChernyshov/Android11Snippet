package com.example.post30.ui.screen.newfeatures.network

import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO
import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA
import android.telephony.TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE
import android.telephony.TelephonyManager
import androidx.annotation.StringRes
import com.example.post30.R

enum class ConnectionStatus(@StringRes val stringRes: Int) {
    WIFI_4(R.string.network_wifi4),
    WIFI_5(R.string.network_wifi5),
    WIFI_6(R.string.network_wifi6),
    WIFI_6E(R.string.network_wifi6e),
    WIFI_7(R.string.network_wifi7), // Future proofing
    WIFI_OTHER(R.string.network_wifi_other),

    // Cellular Specific (prioritizing actual NR, then overrides)
    CELLULAR_5G_NR_STANDALONE(R.string.network_5g),
    CELLULAR_5G_NR_NSA(R.string.network_5g_nsa),
    CELLULAR_5G_NR_NSA_MMWAVE(R.string.network_5g_mmwave),
    CELLULAR_LTE_ADVANCED_PRO(R.string.network_5g_lte_advanced_pro), // LTE underlying, but 5G-like display
    CELLULAR_LTE(R.string.network_4g_lte),
    CELLULAR_3G(R.string.network_3g), // Example for older tech
    CELLULAR_2G(R.string.network_2g), // Example for older tech
    CELLULAR_IWLAN(R.string.network_iwlan), // Wi-Fi calling over cellular connection
    CELLULAR_UNKNOWN(R.string.network_cellular_unknown),

    // No Connection / General
    NO_CONNECTION(R.string.network_no_connection),
    UNKNOWN(R.string.network_unknown);

    companion object {
        fun fromCellularNetworkType(
            networkType: Int,
            networkTypeOverride: Int
        ) = when(networkTypeOverride) {
            OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO -> CELLULAR_LTE_ADVANCED_PRO
            OVERRIDE_NETWORK_TYPE_NR_NSA -> CELLULAR_5G_NR_NSA
            OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE -> CELLULAR_5G_NR_NSA_MMWAVE
            else -> when(networkType) {
                TelephonyManager.NETWORK_TYPE_NR -> CELLULAR_5G_NR_STANDALONE
                TelephonyManager.NETWORK_TYPE_LTE -> CELLULAR_LTE
                TelephonyManager.NETWORK_TYPE_IWLAN -> CELLULAR_IWLAN
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_UMTS -> CELLULAR_3G

                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT -> CELLULAR_2G

                else -> CELLULAR_UNKNOWN
            }
        }
    }
}