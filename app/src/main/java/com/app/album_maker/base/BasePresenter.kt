package com.app.album_maker.base

interface BasePresenter<in V : BaseView> {
    fun attachView(view: V?)
    fun detachView()
}