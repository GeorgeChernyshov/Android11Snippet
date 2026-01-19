package com.example.post30.ui.components

import android.os.Build
import android.view.WindowInsets
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.post30.ui.theme.Android11SnippetTheme

@Composable
fun AppBar(name: String, modifier: Modifier = Modifier) {
    val topInset = when (Build.VERSION.SDK_INT) {
        Build.VERSION_CODES.R -> LocalView.current
            .rootWindowInsets
            .getInsets(WindowInsets.Type.systemBars())
            .top

        else -> LocalView.current
            .rootWindowInsets
            .stableInsetTop
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxWidth()
            .padding(top = topInset.dp / LocalDensity.current.density)
            .padding(16.dp)
    ) {
        Text(
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h4,
            text = name,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    Android11SnippetTheme {
        AppBar("Title")
    }
}