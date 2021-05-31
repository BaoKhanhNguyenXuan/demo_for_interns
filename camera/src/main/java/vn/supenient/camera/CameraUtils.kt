package vn.supenient.camera

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmssSSS",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )
    }

    fun resizeBitmap(bitmap: Bitmap, maxPixel: Double): Bitmap {
        val scale: Double = kotlin.math.sqrt(maxPixel / (bitmap.width * bitmap.height).toDouble())
        val newWith: Int = kotlin.math.floor(bitmap.width * scale).toInt()
        val newHeight: Int = kotlin.math.floor(bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWith, newHeight, true)
    }

    fun getMediaFromStorage(context: Context, maxVideoLength: Int = 0): ArrayList<String> {
        val listItem = ArrayList<String>()
        if (maxVideoLength > 0) {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
            )
            val sortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"
            val query = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, contentUri)
                    val time =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    val timeInMillisec = java.lang.Long.parseLong(time)
                    retriever.release()
                    if (timeInMillisec <= maxVideoLength * 1000) {
                        val realPath = getRealPath(context, contentUri)
                        if (realPath.isNotEmpty())
                            listItem.add(realPath)
                    }
                }
            }
        } else {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATA
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            val query = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val realPath = getRealPath(context, contentUri)
                    if (realPath.isNotEmpty())
                        listItem.add(realPath)
                }
            }
        }
        return listItem
    }

    /**
     * @param context ngữ cảnh
     * @param bitmap nội dung cần lưu
     *
     * Tạo file mới và lưu nội dung từ bitmap
     */
    private fun saveToNewFile(context: Context, bitmap: Bitmap): File {
        val file = createImageFile(context)
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

fun Bitmap.rotate(degree: Int): Bitmap {
    // Initialize a new matrix
    val matrix = Matrix()

    // Rotate the bitmap
    matrix.postRotate(degree.toFloat())

    // Resize the bitmap
    val scaledBitmap = Bitmap.createScaledBitmap(
        this,
        width,
        height,
        true
    )

    // Create and return the rotated bitmap
    return Bitmap.createBitmap(
        scaledBitmap,
        0,
        0,
        scaledBitmap.width,
        scaledBitmap.height,
        matrix,
        true
    )
}