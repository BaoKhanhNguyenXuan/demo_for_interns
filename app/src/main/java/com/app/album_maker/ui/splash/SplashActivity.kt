package com.app.album_maker.ui.splash

import android.content.Intent
import android.os.Handler
import com.app.album_maker.R
import com.app.album_maker.base.BaseActivity
import com.app.album_maker.ui.authentication.AuthenticationActivity

class SplashActivity : BaseActivity() {

    private lateinit var handler: Handler

    override val layoutResId: Int
        get() = R.layout.activity_splash

    override fun setupView() {
        handler = Handler()
        handler.postDelayed({
            goToLogin()
        }, 1000L)
    }

    private fun goToLogin() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
