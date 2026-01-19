package com.example.post30.ui.screen.newfeatures.text

import android.animation.ValueAnimator
import android.graphics.Insets
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowInsetsAnimationControlListener
import android.view.WindowInsetsAnimationController
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.post30.R
import com.example.post30.ui.components.AppBar
import com.example.post30.ui.navigation.Screen

@Composable
fun TextScreen() {
    val view = LocalView.current
    val editTextRef = remember { mutableStateOf<EditText?>(null) }
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var imeBottomPaddingPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    DisposableEffect(view, editTextRef.value) {
        val imeAnimationCallback = object : WindowInsetsAnimation.Callback(
            DISPATCH_MODE_STOP
        ) {
            override fun onProgress(insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>): WindowInsets {
                val imeBottom = insets
                    .getInsets(WindowInsets.Type.ime())
                    .bottom

                imeBottomPaddingPx = imeBottom

                return insets
            }

            override fun onEnd(animation: WindowInsetsAnimation) {
                super.onEnd(animation)
                // Ensure padding is reset if IME is fully hidden
                if (!view.rootWindowInsets.isVisible(WindowInsets.Type.ime())) {
                    imeBottomPaddingPx = 0
                }
            }
        }

        editTextRef.value?.setWindowInsetsAnimationCallback(imeAnimationCallback)

        onDispose {
            editTextRef.value?.setWindowInsetsAnimationCallback(null)
        }
    }

    Scaffold(
        topBar = {
            AppBar(stringResource(Screen.Text.resourceId))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        EditText(context).apply {
                            hint = context.getString(R.string.text_ime_edittext_label)
                            editTextRef.value = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Keep some spacing
                ) { editText ->
                    editText.setText(text.text)
                    editText.setSelection(text.selection.start, text.selection.end)
                }

                Button(onClick = {
                    view.windowInsetsController?.show(WindowInsets.Type.ime())
                }) {
                    Text(stringResource(R.string.text_ime_show_button))
                }

                Button(onClick = {
                    view.windowInsetsController?.hide(WindowInsets.Type.ime())
                }) {
                    Text(stringResource(R.string.text_ime_hide_button))
                }

                Button(onClick = {
                    val animationDuration = 1000L
                    val interpolator = AccelerateInterpolator()

                    view.windowInsetsController?.controlWindowInsetsAnimation(
                        WindowInsets.Type.ime(),
                        animationDuration,
                        interpolator,
                        null,
                        createWindowInsetsAnimationControlListener(
                            view,
                            animationDuration,
                            interpolator
                        )
                    )
                }) {
                    Text("Test")
                }

                Image(
                    painter = painterResource(R.drawable.big_floppa),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = with(density) {
                            imeBottomPaddingPx.toDp()
                        })
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.R)
fun createWindowInsetsAnimationControlListener(
    view: View,
    animationDuration: Long,
    interpolator: Interpolator
): WindowInsetsAnimationControlListener {
    return object : WindowInsetsAnimationControlListener {

        private var animator: ValueAnimator? = null

        override fun onReady(
            controller: WindowInsetsAnimationController,
            types: Int
        ) {
            val animationEndBottom = getMaximumImeHeight(view)

            // Set the controller's initial state (fully hidden and transparent)
            controller.setInsetsAndAlpha(
                Insets.of(0, 0, 0, 0),
                0f,
                0f
            )

            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                this.duration = animationDuration
                this.interpolator = interpolator
                addUpdateListener { anim ->
                    val fraction = anim.animatedFraction
                    val interpolatedBottom = (animationEndBottom * fraction).toInt()
                    val interpolatedAlpha = 1f

                    // Update the IME's insets and alpha for this frame
                    controller.setInsetsAndAlpha(
                        Insets.of(0, 0, 0, interpolatedBottom),
                        interpolatedAlpha,
                        fraction
                    )
                }

                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        controller.finish(true) // Crucial: Release control when your animation is done
                    }

                    override fun onAnimationCancel(animation: android.animation.Animator) {
                        controller.finish(false) // Release control, animation cancelled
                    }
                })

                start() // Start the animation
            }
        }

        override fun onFinished(controller: WindowInsetsAnimationController) {
            animator?.cancel()
        }

        override fun onCancelled(controller: WindowInsetsAnimationController?) {
            animator?.cancel()
        }
    }
}

fun getMaximumImeHeight(view: View): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return view.rootWindowInsets
            ?.getInsets(WindowInsets.Type.ime())
            ?.bottom
            ?: 0
    }

    return 1000
}