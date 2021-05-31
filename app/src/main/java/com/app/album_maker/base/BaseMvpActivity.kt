package com.app.album_maker.base

import android.app.ProgressDialog
import com.app.album_maker.R
import com.app.album_maker.utils.Utils

abstract class BaseMvpActivity<V : BaseView, P : BasePresenter<V>> : BaseActivity(), BaseView {
    var loadingPopup: ProgressDialog? = null
    var presenter: P? = null

    override fun onStart() {
        super.onStart()
        attachView()
    }

    override fun onStop() {
        super.onStop()
        detachView()
    }

    private fun attachView() {
        if (presenter == null) {
            presenter = initMvpPresenter()
        }
        presenter?.attachView(this as V) //this cast to V
    }

    private fun detachView() {
        presenter?.detachView()
    }

    abstract fun initMvpPresenter(): P

    override fun onError(messageId: Int) {
        Utils.showCommonError(this, messageId)
    }

    override fun showLoading() {
        if (!isDestroyed && loadingPopup?.isShowing != true) {
            runOnUiThread {
                loadingPopup = ProgressDialog.show(
                    this,
                    "",
                    getString(R.string.msg_loading),
                    true,
                    false
                )
            }
        }
    }

    override fun showLoading(messageId: Int) {
        if (!isDestroyed && loadingPopup?.isShowing != true) {
            runOnUiThread {
                loadingPopup = ProgressDialog.show(
                    this,
                    "",
                    getString(messageId),
                    true,
                    false
                )
            }
        }
    }

    //ẩn dialog
    override fun hideLoading() {
        loadingPopup?.cancel()
    }
}