package com.example.post30.ui.screen.newfeatures.media

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun MediaScreen() {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val mediaSession = remember {
        MediaSessionCompat(context, MEDIA_SCREEN_TAG)
    }

    val mediaSessionCallback = remember {
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()

                playAudio(
                    mediaPlayer = mediaPlayer,
                    mediaSession = mediaSession
                )
            }

            override fun onPause() {
                super.onPause()

                pauseAudio(
                    mediaPlayer = mediaPlayer,
                    mediaSession = mediaSession
                )
            }

            override fun onStop() {
                super.onStop()

                stopAudio(
                    mediaPlayer = mediaPlayer,
                    mediaSession = mediaSession
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        mediaPlayer.apply {
            setOnCompletionListener {
                stopAudio(
                    mediaPlayer = this,
                    mediaSession = mediaSession
                )
            }
        }

        mediaSession.apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                                or PlaybackStateCompat.ACTION_PAUSE
                    ).build()
            )

            setCallback(mediaSessionCallback)
            isActive = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            mediaSession.release()
        }
    }

    val pickAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            mediaPlayer.setDataSource(context, uri)
            mediaPlayer.prepare()

            var title = context.getString(R.string.media_default_audio_title)
            context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor
                        .getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (nameIndex != -1) {
                        title = cursor.getString(nameIndex)
                    }
                }
            }

            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                    .build()
            )

            playAudio(
                mediaPlayer = mediaPlayer,
                mediaSession = mediaSession
            )
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Media.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        R.string.media_hint_new
                    else R.string.media_hint_old
                ))
                Button(
                    onClick = {
                        pickAudioLauncher.launch("audio/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.media_play_button))
                }

                Button(
                    onClick = {
                        stopAudio(
                            mediaPlayer = mediaPlayer,
                            mediaSession = mediaSession
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.media_stop_button))
                }
            }
        }
    )
}

private fun playAudio(
    mediaPlayer: MediaPlayer,
    mediaSession: MediaSessionCompat
) {
    mediaPlayer.start()
    mediaSession.setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PLAYING,
                mediaPlayer.currentPosition.toLong(),
                1.0f
            )
            .setActions(
                PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_STOP
            ).build()
    )
}

private fun pauseAudio(
    mediaPlayer: MediaPlayer,
    mediaSession: MediaSessionCompat
) {
    mediaPlayer.pause()

    mediaSession.setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PAUSED,
                mediaPlayer.currentPosition.toLong(),
                1.0f
            )
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_STOP
            )
            .build()
    )
}

private fun stopAudio(
    mediaPlayer: MediaPlayer,
    mediaSession: MediaSessionCompat
) {
    mediaPlayer.stop()

    mediaSession.setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
            .setActions(PlaybackStateCompat.ACTION_PLAY)
            .build()
    )
}

private const val MEDIA_SCREEN_TAG = "MediaScreen"