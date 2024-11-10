package io.radev.lilly.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


fun Uri.getFileFromUri(context: Context, uri: Uri): File? {
    val contentResolver: ContentResolver = context.contentResolver
    val tempFile = createTempFile(context)

    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

// Create a temporary file
fun createTempFile(context: Context): File {
    val tempFile = File(context.cacheDir, "temp_image_upload.jpg")
    tempFile.createNewFile()
    return tempFile
}
