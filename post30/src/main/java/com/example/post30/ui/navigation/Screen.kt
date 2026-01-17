package com.example.post30.ui.navigation

import androidx.annotation.StringRes
import com.example.post30.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Audit : Screen("audit", R.string.label_audit)
    object Conversations : Screen("conversations", R.string.label_conversations)
    object GwpAsan : Screen("gwpAsan", R.string.label_gwp_asan)
    object Location : Screen("location", R.string.label_location)
    object Main : Screen("main", R.string.label_main)
    object Media : Screen("media", R.string.label_media)
    object Network : Screen("network", R.string.label_network)
    object NewFeatures : Screen("newFeatures", R.string.label_new_features)
    object PackageVisibility : Screen(
        "packageVisibility",
        R.string.label_package_visibility
    )

    object Performance : Screen("performance", R.string.label_performance)
    object Permissions : Screen("permissions", R.string.label_permissions)
    object Privacy : Screen("privacy", R.string.label_privacy)
    object Security : Screen("security", R.string.label_security)
    object Storage : Screen("storage", R.string.label_storage)
}