package com.app.album_maker.base

import android.content.Context
import com.app.album_maker.utils.AppPrefs
import com.app.album_maker.utils.SettingOption
import java.util.*


class LocaleHelper {

    fun onAttack(mContext: Context, language: Int? = AppPrefs.shared().setting?.language): Context {
        return updateLocale(mContext, getLanguageString(language))
    }

    fun updateLanguage(mContext: Context, language: Int? = AppPrefs.shared().setting?.language) {
        val locale = Locale(getLanguageString(language))
        val config = mContext.resources.configuration
        Locale.setDefault(locale)
        Locale.getDefault()
        config.setLocale(locale)
        mContext.createConfigurationContext(config)
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
    private fun getLanguageString(language: Int?): String {
        return when (language) {
            1 -> SettingOption.JAPANESE
            2 -> SettingOption.VIETNAMESE
            3 -> SettingOption.ENGLISH
            else -> SettingOption.JAPANESE
        }
    }
}

