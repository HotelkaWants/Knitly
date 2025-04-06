package com.hotelka.knitlyWants

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.PathNode
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

fun extractUrls(text: String): List<LinkInfos> {
    val matcher = urlPattern.matcher(text)
    var matchStart: Int
    var matchEnd: Int
    val links = arrayListOf<LinkInfos>()
    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()
        var url = text.substring(matchStart, matchEnd)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "https://$url"
        links.add(LinkInfos(url, matchStart, matchEnd))
    }
    return links
}

data class LinkInfos(
    val url: String,
    val start: Int,
    val end: Int
)

fun Color.toHex(): String {
    return String.format(
        "#%06X",
        this.toArgb() and 0xFFFFFF
    )
}

fun String.toColor(): Color? {
    return try {
        val cleanHex = this.trim().replace("#", "")
        Color(android.graphics.Color.parseColor("#$cleanHex"))
    } catch (e: IllegalArgumentException) {
        null
    }
}
fun formatNumber(number: Int): String {
    return when {
        number < 1000 -> number.toString()
        number < 1000000 -> "%.1fk".format(number / 1000.0)
        else -> "%.1fm".format(number / 1000000.0)
    }.replace(".0", "")
}
fun Long.toTimeString():String{
    val date = Date(this)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
@OptIn(DelicateCoroutinesApi::class)
fun URL.read(): String {
    val connection = this.openConnection()
    val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
    var line: String?
    var result: StringBuilder = StringBuilder()
    while (reader.readLine().also { line = it } != null) {
        result.append(line)
    }
    reader.close()
    return result.toString()
}

fun imageBitmapToByteArray(imageBitmap: ImageBitmap): ByteArray {
    // Step 1: Convert ImageBitmap to Bitmap
    val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

    // Step 2: Convert Bitmap to ByteArray
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // You can use JPEG or other formats
    return stream.toByteArray()
}

fun urlToBitmap(
    scope: CoroutineScope,
    imageURL: String,
    context: Context,
    onError: (error: Throwable) -> Unit,
    onSuccess: (bitmap: Bitmap) -> Unit
) {
    var bitmap: Bitmap? = null
    val loadBitmap = scope.launch(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageURL)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            bitmap = result.image.toBitmap()
        } else if (result is ErrorResult) {
            cancel(result.throwable.localizedMessage ?: "ErrorResult", result.throwable)
        }
    }
    loadBitmap.invokeOnCompletion { throwable ->
        bitmap?.let {
            onSuccess(it)
        } ?: throwable?.let {
            onError(it)
        } ?: onError(Throwable("Undefined Error"))
    }
}

fun PathNode.scaleTo(size: Size): PathNode {
    val originalWidth = 278f
    val originalHeight = 207f

    return when (this) {
        is PathNode.CurveTo ->
            this.copy(
                x1 = x1.scaleToSize(originalWidth, size.width),
                x2 = x2.scaleToSize(originalWidth, size.width),
                x3 = x3.scaleToSize(originalWidth, size.width),
                y1 = y1.scaleToSize(originalHeight, size.height),
                y2 = y2.scaleToSize(originalHeight, size.height),
                y3 = y3.scaleToSize(originalHeight, size.height),
            )
        is PathNode.MoveTo ->
            this.copy(
                x = x.scaleToSize(originalWidth, size.width),
                y = y.scaleToSize(originalHeight, size.height),
            )
        else -> this
    }
}
private fun Float.scaleToSize(
    oldSize: Float,
    newSize: Float,
): Float {
    val ratio = newSize / oldSize
    return this * ratio
}