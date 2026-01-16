package com.example.post30.ui.bubbles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.post30.R
import com.example.post30.ui.screen.newfeatures.conversations.ConversationsActivity
import com.example.post30.ui.screen.newfeatures.conversations.ReplyReceiver
import com.example.post30.ui.theme.Android11SnippetTheme

class BubbleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val messageText = intent.extras?.getString(MESSAGE_TEXT)
        val imgPath = intent.extras?.getString(MESSAGE_IMG_PATH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            setContent { Bubble(messageText, imgPath) }
    }

    companion object {
        const val MESSAGE_TEXT = "text"
        const val MESSAGE_IMG_PATH = "imgPath"
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Bubble(
    message: String?,
    imgPath: String?
) {
    val context = LocalContext.current

    Android11SnippetTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MessageBlock(
                modifier = Modifier.fillMaxWidth(),
                message = message,
                imgPath = imgPath
            )

            ReplyBlock(onSend = {
                val replyIntent = Intent(context, ReplyReceiver::class.java)
                    .putExtra(ReplyReceiver.KEY_TEXT_REPLY, it)

                context.sendBroadcast(replyIntent)

                context.startActivity(
                    Intent(
                        context,
                        ConversationsActivity::class.java
                    )
                )
            })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MessageBlock(
    modifier: Modifier = Modifier,
    message: String?,
    imgPath: String?
) {
    val imageSource = ImageDecoder.createSource(
        LocalContext.current.contentResolver,
        Uri.parse(imgPath)
    )

    Column(modifier = modifier) {
        Text(
            text = message.orEmpty(),
            color = MaterialTheme.colors.onBackground
        )

        Image(
            modifier = Modifier.padding(top = 16.dp),
            bitmap = getCroppedImage(imageSource).asImageBitmap(),
            contentDescription = "image"
        )
    }
}

@Composable
fun ReplyBlock(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Reply") }
        )

        Button(onClick = { onSend.invoke(text) }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Reply"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
fun BubblePreview() {
    Bubble(
        message = "Test",
        imgPath = "android.resource://com.example.post30/" + R.drawable.big_floppa
    )
}

@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
fun MessageBlockPreview() {
    MessageBlock(
        message = "Test",
        imgPath = "android.resource://com.example.post30/" + R.drawable.big_floppa
    )
}

@Preview(showBackground = true)
@Composable
fun ReplyBlockPreview() {
    ReplyBlock()
}

@RequiresApi(Build.VERSION_CODES.P)
private fun getCroppedImage(imageSource: ImageDecoder.Source): Bitmap {
    return ImageDecoder.decodeBitmap(imageSource) { decoder, info, source ->
        decoder.setPostProcessor {
            val path = Path()
            path.fillType = Path.FillType.INVERSE_EVEN_ODD
            path.addRoundRect(
                0f,
                0f,
                it.width.toFloat(),
                it.height.toFloat(),
                40f,
                40f,
                Path.Direction.CW
            )

            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = Color.TRANSPARENT
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            it.drawPath(path, paint)

            return@setPostProcessor PixelFormat.UNKNOWN
        }
    }
}