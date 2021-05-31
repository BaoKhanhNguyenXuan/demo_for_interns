package com.app.album_maker.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.album_maker.utils.Permission
import com.app.album_maker.utils.Utils

abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutResId: Int
    private val localeManager = LocaleHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutResId)
        setupView()
    }

    abstract fun setupView()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeManager.onAttack(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val wrapContext = localeManager.onAttack(this)
        super.onConfigurationChanged(wrapContext.resources.configuration)
    }

}
