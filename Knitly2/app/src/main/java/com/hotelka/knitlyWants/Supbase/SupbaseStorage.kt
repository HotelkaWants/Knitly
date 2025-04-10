package com.hotelka.knitlyWants.Supbase

import android.content.Context
import android.net.Uri
import com.hotelka.knitlyWants.supabase
import com.hotelka.knitlyWants.userData
import io.github.jan.supabase.storage.storage

import io.github.jan.supabase.storage.upload
import org.apache.commons.lang3.RandomStringUtils
import java.io.File

suspend fun uploadFile(bucket: String, file: ByteArray, extension: String = ".png"): String? {
    val bucket = supabase.storage.from(bucket)

    val fileName = "${userData.value.userId}/" + RandomStringUtils.randomAlphanumeric(15) + extension
    bucket.upload(
        path = fileName,
        data = file,

    )
    return fileName
}

fun getData(bucket: String, path: String): String {
    return supabase.storage.from(bucket).publicUrl(path)
}
fun getFileFromUri(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "temp_image.jpg")
    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return file
}