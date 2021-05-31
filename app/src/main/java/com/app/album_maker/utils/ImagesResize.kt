package com.app.album_maker.utils

import android.content.Context
import android.os.Handler
import android.os.Looper


class ImagesResize(
    val context: Context,
    val listPath: ArrayList<String>,
    val resizeSuccess: ResizeSuccess
) : Runnable {
    private val mHandler = Handler(Looper.getMainLooper())

    override fun run() {
        val listResult = arrayListOf<String>()
        for (path in listPath)
            listResult.add(ModifyImage().resizeImage(context, path).absolutePath)
        mHandler.post {
            resizeSuccess.onResizeSuccess(listResult)
        }
    }
}

interface ResizeSuccess{
    fun onResizeSuccess(listResult: ArrayList<String>)
}