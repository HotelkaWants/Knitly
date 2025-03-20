package com.hotelka.knitlyWants

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import kotlinx.io.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID

fun saveImageToExternalStorage(bitmap:Bitmap):Uri{
    val path = Environment.getExternalStorageDirectory().toString()

    val file = File(path, "${UUID.randomUUID()}.jpg")

    try {
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException){
        e.printStackTrace()
    }

    // Return the saved image path to uri
    return Uri.parse(file.absolutePath)
}
