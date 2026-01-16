package com.example.post30.ui.screen.privacy.storage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.privacy.storage.RequestCodes.EDIT_REQUEST_CODE
import java.util.ArrayList

@Composable
fun StorageScreen(viewModel: StorageViewModel = viewModel()) {
    Scaffold(
        topBar = { AppBar(name = stringResource(id = Screen.Storage.resourceId)) },
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                FavoriteMediaButton(viewModel.state.value.showFavoriteUnavailableText) {
                    viewModel.showFavoriteUnavailable()
                }

                DocumentAccessView(modifier = Modifier.padding(top = 16.dp))
            }
        }
    )
}

@Composable
fun FavoriteMediaButton(
    showFavoriteUnavailableText: Boolean,
    onFavoriteUnavailable: () -> Unit = {}
) {
    val context = LocalContext.current

    val pickLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uris = ArrayList<Uri>()

            when {
                result.data?.clipData != null -> {
                    val clipData = result.data?.clipData!!
                    for (i in 0 until clipData.itemCount)
                        uris.add(clipData.getItemAt(i).uri)
                }

                result.data?.data != null -> uris.add(result.data?.data!!)

                else -> return@rememberLauncherForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val editPendingIntent = MediaStore.createFavoriteRequest(context.contentResolver, uris, true)

                // Launch a system prompt requesting user permission for the operation.
                startIntentSenderForResult(
                    context as Activity,
                    editPendingIntent.intentSender,
                    EDIT_REQUEST_CODE,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            } else onFavoriteUnavailable.invoke()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            Text(stringResource(R.string.storage_favorite_available))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                pickLauncher.launch(intent)
            }
        ) {
            Text(text = stringResource(id = R.string.storage_select_favorite_media))
        }

        if (showFavoriteUnavailableText) {
            Text(text = stringResource(id = R.string.storage_favorite_unavailable))
        }
    }
}

@Composable
fun DocumentAccessView(modifier: Modifier = Modifier) {
    DocumentAccessViewVersioned(
        newVersion = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R),
        modifier = modifier
    )
}

@Composable
fun DocumentAccessViewVersioned(newVersion: Boolean, modifier: Modifier = Modifier) {
    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) {}

    val openDocumentTreeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) {}

    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = stringResource(
            id = if (newVersion) R.string.storage_open_document_new_hint
                else R.string.storage_open_document_old_hint
        ))

        Button(onClick = { openDocumentLauncher.launch(arrayOf("text/*")) }) {
            Text(text = stringResource(id = R.string.storage_open_document_test))
        }

        Text(modifier = Modifier.padding(top = 16.dp),
            text = stringResource(
                id = if (newVersion) R.string.storage_open_document_tree_new_hint
                    else R.string.storage_open_document_tree_old_hint
            )
        )

        Button(onClick = { openDocumentTreeLauncher.launch(Uri.EMPTY) }) {
            Text(text = stringResource(id = R.string.storage_open_document_tree_test))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteMediaButtonPreview() {
    FavoriteMediaButton(true)
}

@Preview(showBackground = true)
@Composable
fun DocumentAccessViewPreview() {
    DocumentAccessViewVersioned(newVersion = true)
}

object RequestCodes {
    const val EDIT_REQUEST_CODE = 15452
    const val PICK_FILES_CODE = 24658
}