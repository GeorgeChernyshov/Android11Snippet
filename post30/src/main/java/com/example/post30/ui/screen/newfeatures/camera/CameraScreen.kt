package com.example.post30.ui.screen.newfeatures.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen
import com.example.post30.ui.screen.newfeatures.conversations.NotificationHelper
import java.util.Collections

private const val TAG = "CameraScreen"

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Camera-related states
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var cameraDeviceId: String? by remember { mutableStateOf(null) } // Store the ID of the opened camera device

    // UI states
    var currentRestriction by remember { mutableStateOf(CameraDevice.AUDIO_RESTRICTION_NONE) }
    var cameraLog by remember {
        mutableStateOf(context.getString(R.string.camera_log_not_active))
    }

    var openedCameraDevice: CameraDevice? by remember { mutableStateOf(null) }
    var captureSession: CameraCaptureSession? by remember { mutableStateOf(null) }
    var previewSize: Size? by remember { mutableStateOf(null) }
    var previewSurface: Surface? by remember { mutableStateOf(null) } // Surface for TextureView
    var textureView: TextureView? by remember { mutableStateOf(null) }

    val cameraBackgroundThread = remember {
        HandlerThread("CameraBackground")
            .apply { start() }
    }

    val cameraBackgroundHandler = remember {
        Handler(cameraBackgroundThread.looper)
    }

    // Permission launcher for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
        if (!isGranted) {
            cameraLog = context.getString(R.string.camera_log_permission_denied)
            Log.w(TAG, cameraLog)
        }
    }

    val cameraDeviceStateCallback = remember(cameraBackgroundHandler) {
        object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "CameraDevice opened: ${camera.id}")
                openedCameraDevice = camera
                cameraLog = context.getString(
                    R.string.camera_log_device_is_opened,
                    camera.id
                )

                textureView?.surfaceTexture?.let { surfaceTexture ->
                    // We need to set the buffer size based on the chosen preview size
                    previewSize?.let { size ->
                        surfaceTexture.setDefaultBufferSize(size.width, size.height)
                        previewSurface = Surface(surfaceTexture)
                        createCameraPreviewSession(
                            cameraDevice = camera,
                            previewSurface = previewSurface!!,
                            backgroundHandler = cameraBackgroundHandler,
                            onSessionCreated = { session -> captureSession = session },
                            onSessionFailed = {
                                cameraLog = context.getString(
                                    R.string.camera_log_failed_to_create_capture_session
                                )
                            }
                        )
                    } ?: run {
                        cameraLog = context.getString(
                            R.string.camera_log_preview_size_not_determined
                        )

                        Log.e(TAG, "Preview size is null when creating session.")
                    }
                } ?: run {
                    cameraLog = context.getString(R.string.camera_log_textureview_not_ready)
                    Log.e(TAG, "TextureView surfaceTexture is null.")
                }
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.w(TAG, "CameraDevice disconnected: ${camera.id}")
                camera.close()
                openedCameraDevice = null
                captureSession = null
                cameraLog = context.getString(
                    R.string.camera_log_disconnected,
                    camera.id
                )
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.e(TAG, "CameraDevice error: ${camera.id}, error $error")
                camera.close()
                openedCameraDevice = null
                captureSession = null
                cameraLog = context.getString(
                    R.string.camera_log_error,
                    camera.id,
                    error
                )
            }
        }
    }

    LaunchedEffect(
        cameraPermissionGranted,
        context,
        cameraDeviceStateCallback,
        cameraBackgroundHandler
    ) {
        if (cameraPermissionGranted) {
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                val backCameraId = manager.cameraIdList.firstOrNull { cameraId ->
                    val characteristics = manager.getCameraCharacteristics(cameraId)
                    val isBackFacing = characteristics.get(
                        CameraCharacteristics.LENS_FACING
                    ) == CameraCharacteristics.LENS_FACING_BACK

                    val isAvailable = true

                    // Determine optimal preview size
                    val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val outputSizes = map?.getOutputSizes(SurfaceTexture::class.java)
                    if (outputSizes != null && outputSizes.isNotEmpty()) {
                        previewSize = outputSizes.firstOrNull { size ->
                            // Simple heuristic: choose a size that's close to 1080p or smaller
                            size.width <= 1920 && size.height <= 1080
                        } ?: outputSizes[0] // Fallback to first size
                        Log.d(TAG, "Selected preview size: ${previewSize?.width}x${previewSize?.height}")
                    } else {
                        Log.w(TAG, "No output sizes for SurfaceTexture on camera $cameraId")
                        previewSize = null // Cannot proceed without a size
                    }

                    isBackFacing && isAvailable && previewSize != null // All conditions for opening
                }

                backCameraId?.let {
                    manager.openCamera(
                        it,
                        cameraDeviceStateCallback,
                        cameraBackgroundHandler
                    )
                }
            } catch (e: CameraAccessException) {
                cameraLog = context.getString(
                    R.string.camera_log_failed_to_access,
                    e.message
                )

                Log.e(TAG, "Camera access exception", e)
            } catch (e: SecurityException) {
                cameraLog = context.getString(
                    R.string.camera_log_permission_required,
                    e.message
                )

                Log.e(TAG, "Security exception", e)
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Request camera permission on launch
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Disposing CameraScreen: Closing CameraDevice, ToneGenerator, and background thread.")
            captureSession?.close()
            captureSession = null
            openedCameraDevice?.close()
            openedCameraDevice = null
            cameraBackgroundThread.quitSafely()
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Camera.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val restrictionText = stringResource(
                    when (currentRestriction) {
                        CameraDevice.AUDIO_RESTRICTION_NONE ->
                            R.string.camera_restriction_none

                        CameraDevice.AUDIO_RESTRICTION_VIBRATION ->
                            R.string.camera_restriction_vibration

                        CameraDevice.AUDIO_RESTRICTION_VIBRATION_SOUND ->
                            R.string.camera_restriction_sound_and_vibration

                        else -> R.string.camera_restriction_unknown
                    }
                )

                Text(
                    text = stringResource(
                        R.string.camera_current_restriction,
                        restrictionText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(
                        R.string.camera_status,
                        cameraLog
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (cameraPermissionGranted) {
                    // CameraX Preview Composable
                    TextureView(
                        setTextureView = { textureView = it }
                    )
                } else {
                    Text(stringResource(R.string.camera_no_permission))
                }

                // --- Buttons for setting camera audio restriction ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = {
                        openedCameraDevice?.let { cameraDevice ->
                            cameraDevice.setCameraAudioRestriction(
                                CameraDevice.AUDIO_RESTRICTION_NONE
                            )

                            currentRestriction = CameraDevice.AUDIO_RESTRICTION_NONE
                            Log.d(TAG, "Restriction set to NONE for device ${cameraDevice.id}.")
                        } ?: run {
                            cameraLog = "CameraDevice not opened to set restriction."
                            Log.w(TAG, cameraLog)
                        }
                    }) {
                        Text(stringResource(R.string.camera_button_none))
                    }

                    Button(onClick = {
                        openedCameraDevice?.let { cameraDevice ->
                            cameraDevice.setCameraAudioRestriction(
                                CameraDevice.AUDIO_RESTRICTION_VIBRATION
                            )

                            currentRestriction = CameraDevice.AUDIO_RESTRICTION_VIBRATION
                            Log.d(
                                TAG,
                                "Restriction set to MUTE VIBRATION for device ${cameraDevice.id}."
                            )
                        } ?: run {
                            cameraLog = context.getString(R.string.camera_log_not_opened)
                            Log.w(TAG, cameraLog)
                        }
                    }) {
                        Text(stringResource(R.string.camera_button_mute_vibration))
                    }

                    Button(onClick = {
                        openedCameraDevice?.let { cameraDevice ->
                            cameraDevice.setCameraAudioRestriction(
                                CameraDevice.AUDIO_RESTRICTION_VIBRATION_SOUND
                            )

                            currentRestriction = CameraDevice.AUDIO_RESTRICTION_VIBRATION_SOUND
                            Log.d(
                                TAG,
                                "Restriction set to MUTE SOUND & VIBRATION for device ${cameraDevice.id}."
                            )
                        } ?: run {
                            cameraLog = context.getString(R.string.camera_log_not_opened)
                            Log.w(TAG, cameraLog)
                        }
                    }) {
                        Text(stringResource(R.string.camera_button_mute_sound_vibration))
                    }
                }

                Button(onClick = {
                    NotificationHelper(context).showLoudNotification()
                    Log.d(TAG, "Sent notification.")
                }) {
                    Text(stringResource(R.string.camera_button_send_notification))
                }
            }
        }
    )
}

private fun createCameraPreviewSession(
    cameraDevice: CameraDevice,
    previewSurface: Surface,
    backgroundHandler: Handler,
    onSessionCreated: (CameraCaptureSession) -> Unit,
    onSessionFailed: () -> Unit
) {
    try {
        val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        previewRequestBuilder.addTarget(previewSurface)

        cameraDevice.createCaptureSession(
            Collections.singletonList(previewSurface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d(TAG, "CameraCaptureSession configured.")
                    onSessionCreated(session)
                    try {
                        // Auto focus should work by default
                        previewRequestBuilder.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        // Finally, we start displaying the camera preview.
                        session.setRepeatingRequest(
                            previewRequestBuilder.build(),
                            null, // No CaptureCallback needed for simple preview
                            backgroundHandler
                        )
                        Log.d(TAG, "Repeating preview request started.")
                    } catch (e: CameraAccessException) {
                        Log.e(TAG, "Failed to start repeating request", e)
                        onSessionFailed()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "CameraCaptureSession configuration failed.")
                    onSessionFailed()
                }
            }, backgroundHandler
        )
    } catch (e: CameraAccessException) {
        Log.e(TAG, "Failed to create capture session", e)
        onSessionFailed()
    }
}

@Composable
fun TextureView(setTextureView: (TextureView) -> Unit) {
    AndroidView(
        factory = { ctx ->
            FrameLayout(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                val textureView = TextureView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                addView(textureView)
                setTextureView(textureView)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(bottom = 16.dp),
        update = {}
    )
}