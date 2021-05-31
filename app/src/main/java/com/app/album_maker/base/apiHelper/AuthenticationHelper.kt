package com.app.album_maker.base.apiHelper

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import com.app.album_maker.R
import com.app.album_maker.ui.authentication.AuthenticationActivity
import com.app.album_maker.utils.AppPrefs
import com.app.album_maker.utils.HttpCode
import com.app.album_maker.utils.Utils

object AuthenticationHelper {

    fun handleAuthentication(context: Context?, code: Int?, message: String? = null, callback:(()-> Unit)? = null) {
        when(code) {
            HttpCode.FORBIDDEN -> {
                if (context !is AuthenticationActivity) {
                    Utils.showAlert(context,
                        messageId = R.string.error_system_error,
                        positiveTextId = R.string.confirm_ok,
                        positiveListener = DialogInterface.OnClickListener { dialog, which ->
                            context?.startActivity(Intent(context, AuthenticationActivity::class.java))
                            AppPrefs.shared().clearData()
                        })
                } else {
                    callback?.invoke()
                }
            }
            HttpCode.UNAUTHORIZED -> {
                if (context !is AuthenticationActivity) {
                    Utils.showAlert(context,
                        messageId = R.string.session_was_expired,
                        positiveTextId = R.string.confirm_ok,
                        positiveListener = DialogInterface.OnClickListener { dialog, which ->
                            context?.startActivity(Intent(context, AuthenticationActivity::class.java))
                            AppPrefs.shared().clearData()
                        })
                } else {
                    callback?.invoke()
                }
            }
            HttpCode.SERVICE_TIMEOUT, HttpCode.SERVICE_UNAVAILABLE -> {
                showAlertNoInternet(context)
            }
            else -> {
//                Toast.makeText(context, message ?: "unknown error", Toast.LENGTH_SHORT)
//                    .show()
                callback?.invoke()
            }
        }
    }


    private fun showAlertNoInternet(context: Context?){
        Utils.showAlert(
            context = context,
            messageId = R.string.connect_internet_fail,
            positiveTextId = R.string.confirm_ok
        )
    }
}