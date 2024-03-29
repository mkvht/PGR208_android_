package com.example.pgr208_android_eksamen.utilities

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapUtility {

    fun getBitmap(context: Context, id: Int?, uri: String?, decoder: (Context, Int?, String?) -> Bitmap): Bitmap {
        return decoder(context, id, uri)
    }

    fun bitmapArray(bitmap : Bitmap) : ByteArray{
        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }

    //FROM https://stackoverflow.com/questions/7769806/convert-bitmap-to-file
    fun bitmapFile(bitmap : Bitmap, filename : String, context: Context) : File {


        val bitmapdata = bitmapArray(bitmap)

        val file = File(context.cacheDir, filename)
        file.createNewFile()

        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(bitmapdata)
        fileOutputStream.flush()
        fileOutputStream.close()

        return file
    }

    @Suppress("DEPRECATION")
    fun uriToBitmap(context: Context, @Suppress("UNUSED_PARAMETER") id: Int?, uri: String?): Bitmap {
        val image: Bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uri))
        return image
    }
}