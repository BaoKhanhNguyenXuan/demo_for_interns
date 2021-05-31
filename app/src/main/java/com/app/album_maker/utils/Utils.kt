package com.app.album_maker.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.SpeechRecognizer
import android.text.InputFilter
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.album_maker.R
import com.app.album_maker.base.dialog.TextFromSpeech
import com.app.album_maker.base.voicerecord.SpeechRecognizerManager
import com.app.album_maker.base.voicerecord.onSpeechResultsReady
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


object Utils {

    fun hideKeyboard(activity: Activity?) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity?.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeybroadFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSystemError(context: Context?) {
        showCommonError(context, messageId = R.string.error_system_error)
    }

    fun showCommonError(context: Context?, messageId: Int) {
        showAlert(
            context,
            messageId = messageId,
            positiveTextId = R.string.confirm_ok
        )
    }

    fun showAlert(
        context: Context?,
        titleId: Int = 0,
        messageId: Int,
        positiveTextId: Int, positiveListener: DialogInterface.OnClickListener? = null,
        negativeTextId: Int? = null, negativeListener: DialogInterface.OnClickListener? = null
    ) {
        val dialog = AlertDialog.Builder(context).setMessage(messageId)
        dialog.setCancelable(false)
        if (titleId != 0) {
            dialog.setTitle(titleId)
        }
        if (positiveListener != null) {
            dialog.setPositiveButton(positiveTextId, positiveListener)
        } else {
            dialog.setPositiveButton(positiveTextId) { sDialog, _ ->
                sDialog.cancel()
            }
        }
        negativeTextId?.let {
            if (negativeListener != null) {
                dialog.setNegativeButton(it, negativeListener)
            } else {
                dialog.setNegativeButton(it) { sDialog, _ ->
                    sDialog.cancel()
                }
            }
        }
        dialog.show()
    }

    fun showAlert(
        context: Context?,
        title: String = "",
        message: String,
        positiveTextId: Int, positiveListener: DialogInterface.OnClickListener? = null,
        negativeTextId: Int? = null, negativeListener: DialogInterface.OnClickListener? = null
    ) {
        val dialog = AlertDialog.Builder(context).setMessage(message)
        dialog.setCancelable(false)
        if (title.isNotEmpty()) {
            dialog.setTitle(title)
        }
        if (positiveListener != null) {
            dialog.setPositiveButton(positiveTextId, positiveListener)
        } else {
            dialog.setPositiveButton(positiveTextId) { sDialog, _ ->
                sDialog.cancel()
            }
        }
        negativeTextId?.let {
            if (negativeListener != null) {
                dialog.setNegativeButton(it, negativeListener)
            } else {
                dialog.setNegativeButton(it) { dialog, _ ->
                    dialog.cancel()
                }
            }
        }
        dialog.show()
    }


    fun allPermissionsGranted(context: Context?) = Permission.PERMISSION_REQUEST.all {
        context?.let { ctx ->
            ContextCompat.checkSelfPermission(
                ctx,
                it
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    fun emailValid(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$"
        val pat = Pattern.compile(emailRegex)
        return pat.matcher(email).matches()
    }

    fun createImageFile(context: Context): File {
        val timeStamp =
            SimpleDateFormat(
                Strings.TIME_FORMAT,
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

    //create MultipartBody.Part from image
    fun createMultipartBody(
        mediaType: Int,
        media: File
    ): MultipartBody.Part {
        if (mediaType == Constant.UPLOAD_TYPE_VIDEO) {
            val requestFile: RequestBody = media.asRequestBody("video/*".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            return MultipartBody.Part.createFormData("images[]", media.name, requestFile)
        } else {
            val requestFile: RequestBody = media.asRequestBody("image/*".toMediaTypeOrNull())
            // MultipartBody.Part is used to send also the actual file name
            return MultipartBody.Part.createFormData("images[]", media.name, requestFile)
        }
    }

    fun createMultipartType(type: Int = Constant.UPLOAD_TYPE_IMAGE): MultipartBody.Part {
        return MultipartBody.Part.createFormData("type", type.toString())
    }

    fun setEdittextMaxCharacter(editText: EditText, maxChar: Int) {
        editText.filters += InputFilter.LengthFilter(maxChar)
    }

    fun showStartInputEditText(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                editText.setSelection(0, 0)
            }
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxPixel: Double): Bitmap {
        val scale: Double = kotlin.math.sqrt(maxPixel / (bitmap.width * bitmap.height).toDouble())
        val newWith: Int = kotlin.math.floor(bitmap.width * scale).toInt()
        val newHeight: Int = kotlin.math.floor(bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWith, newHeight, true)
    }

    fun disableTouch(context: Context) {
        (context as Activity).window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    fun enableTouch(context: Context) {
        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}