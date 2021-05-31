package com.app.album_maker.base

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.app.album_maker.R
import com.app.album_maker.utils.AppExtension.actionbar
import com.app.album_maker.utils.Permission
import com.app.album_maker.utils.Utils

abstract class BaseFragment<V : BaseView, P : BasePresenter<V>> : Fragment(), BaseView {

    var loadingPopup: ProgressDialog? = null
    var presenter: P? = null

    abstract val layoutResId: Int

    protected open val isHasActionbar: Boolean
        get() = true

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)
        if (layoutResId != 0) {
            view = inflater.inflate(layoutResId, container, false)
        }
        return view
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachView()
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isHasActionbar) {
            actionbar()?.hide()
        } else {
            actionbar()?.show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        detachView()
    }

    private fun attachView() {
        if (presenter == null) {
            presenter = initMvpPresenter()
        }
        presenter?.attachView(this as? V) //this cast to V
    }

    private fun detachView() {
        presenter?.detachView()
    }

    abstract fun initMvpPresenter(): P

    override fun onError(messageId: Int) {
        Utils.showCommonError(context, messageId)
    }

    override fun showLoading() {
        if (isAdded && loadingPopup?.isShowing != true) {
            Handler(Looper.getMainLooper()).post {
                loadingPopup = ProgressDialog.show(
                    context,
                    "",
                    getString(R.string.msg_loading),
                    true,
                    false
                )
            }
        }
    }
    override fun showLoading(messageId: Int) {
        if (isAdded && loadingPopup?.isShowing != true) {
            Handler(Looper.getMainLooper()).post {
                loadingPopup = ProgressDialog.show(
                    context,
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
        Handler(Looper.getMainLooper()).post {
            loadingPopup?.cancel()
        }
    }
}