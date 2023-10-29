package com.example.post30.ui.screen.storage

data class StorageScreenState(val showFavoriteUnavailableText: Boolean) {
    companion object {
        val DEFAULT = StorageScreenState(showFavoriteUnavailableText = false)
    }
}