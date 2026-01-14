package com.example.post30.ui.navigation

import androidx.annotation.StringRes
import com.example.post30.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Audit : Screen("audit", R.string.label_audit)
    object Conversations : Screen("conversations", R.string.label_conversations)
    object Location : Screen("location", R.string.label_location)
    object Media : Screen("media", R.string.label_media)
    object Network : Screen("network", R.string.label_network)
    object PackageVisibility : Screen(
        "packageVisibility",
        R.string.label_package_visibility
    )

    object Permissions : Screen("permissions", R.string.label_permissions)
    object Storage : Screen("storage", R.string.label_storage)
}