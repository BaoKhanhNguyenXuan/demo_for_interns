package vn.supenient.camera

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseLanguageActivity : AppCompatActivity() {

    private val language = CameraSetup.shared().language

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(language?.let { updateLocale(base, it) } ?: base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val wrapContext = language?.let { updateLocale(this, it) } ?: this
        super.onConfigurationChanged(wrapContext.resources.configuration)
    }

    private fun updateLocale(mContext: Context, language: String): Context {
        val locale = Locale(language)
        val res = mContext.resources
        val config = res.configuration
        Locale.setDefault(locale)
        Locale.getDefault()
        config.setLocale(locale)
        return mContext.createConfigurationContext(config)
    }
}