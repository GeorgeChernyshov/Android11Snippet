package com.example.post30.ui.navigation

import androidx.annotation.StringRes
import com.example.post30.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Storage : Screen("storage", R.string.label_storage)
//    object Permissions : Screen("permissions", R.string.label_permissions)
}