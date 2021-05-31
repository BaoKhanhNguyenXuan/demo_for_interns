package com.app.album_maker.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.app.album_maker.utils.Constant.IMAGE_SIZE_1MB
import com.app.album_maker.utils.Constant.IMAGE_SIZE_2MB
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.floor
import kotlin.math.sqrt

class ModifyImage {
    /**
     * @param context ngữ cảnh
     * @param bitmap nội dung cần lưu
     *
     * Tạo file mới và lưu nội dung từ bitmap
     */
    private fun saveToNewFile(context: Context, bitmap: Bitmap): File {
        val file = Utils.createImageFile(context)
        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            Log.e(this::class.java.simpleName, e.message.toString())
        }
        return file
    }

    /**
     * @param context ngữ cảnh
     * @param path nội dung cần thay đổi kích cỡ
     */
    fun resizeImage(context: Context, path: String): File {
        var bitmap = rotateImage(path)!!
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val currentWidth = bitmap.width
        val currentHeight = bitmap.height
        val currentPixel = currentHeight * currentWidth
        val maxPixel = (AppPrefs.shared().setting?.image_size!! * Constant.MAX_BYTE)
        if (currentPixel <= maxPixel) {
            return saveToNewFile(context, bitmap)
        }
        val maxSize = if (AppPrefs.shared().setting?.image_size in IMAGE_SIZE_1MB..IMAGE_SIZE_2MB) {
            (AppPrefs.shared().setting?.image_size!! * Constant.MAX_BYTE).toDouble()
        } else {
            Constant.MAX_BYTE.toDouble()
        }
        bitmap = Utils.resizeBitmap(bitmap, maxSize)
        //Tạo lưu file sau khi resize
        return saveToNewFile(context, bitmap)
    }

    /**
     * @param path đường dẫn tới ảnh cần xoay ảnh
     *
     * Trả về bitmap chứa dữ liệu ảnh đã xoay
     */
    private fun rotateImage(path: String): Bitmap? {
        when (ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                return rotate(path, 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                return rotate(path, 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                return rotate(path, 270f)
            }
        }
        return BitmapFactory.decodeFile(path)
    }

    private fun rotate(path: String, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        var bitmap = BitmapFactory.decodeFile(File(path).absolutePath)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return bitmap
    }

    fun getRealPath(context: Context?, uri: Uri): String {
        if ("com.android.providers.media.documents" == uri.authority) {
            val docId: String = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context?.contentResolver?.query(
                    contentUri, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message!!)
            } finally {
                cursor?.close()
            }
        } else {
            var path = ""
            try {
                context?.contentResolver?.let {
                    val filePartColumns = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor = it.query(uri, filePartColumns, null, null, null)
                    cursor?.moveToFirst()
                    val columnsIndex = cursor?.getColumnIndex(filePartColumns[0]) ?: 0
                    path = cursor?.getString(columnsIndex) ?: " "
                    cursor?.close()
                }
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName, e.message.toString())
            }
            return path
        }
        return ""
    }

}