package com.app.album_maker.base

import androidx.annotation.StringRes

interface BaseView {
    fun onError(@StringRes messageId: Int)

    fun showLoading()

    fun showLoading(messageId: Int)

    fun hideLoading()
}