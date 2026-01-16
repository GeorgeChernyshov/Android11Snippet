package com.example.post30.ui.screen.privacy.storage

data class StorageScreenState(val showFavoriteUnavailableText: Boolean) {
    companion object {
        val DEFAULT = StorageScreenState(showFavoriteUnavailableText = false)
    }
}