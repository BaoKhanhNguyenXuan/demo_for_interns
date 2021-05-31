package com.app.album_maker.base

import java.lang.ref.WeakReference

abstract class BasePresenterImp<V : BaseView> : BasePresenter<V> {
    private var weakView: WeakReference<V?>? = null
    internal val view: V? get() = weakView?.get()

    private var isAttachView: Boolean = weakView?.get() != null

    //Tạo giá trị cho tham số yêu nếu như weakView null
    override fun attachView(view: V?) {
        if (!isAttachView) {
            weakView = WeakReference(view)
        }
    }

    //Giải phóng bộ nhớ
    override fun detachView() {
        weakView?.clear()
        weakView = null
    }
}