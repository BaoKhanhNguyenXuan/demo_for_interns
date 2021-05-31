package com.app.album_maker.utils

import android.content.SharedPreferences
import com.app.album_maker.data.model.SettingModel
import com.app.album_maker.data.model.UserData
import vn.supenient.camera.CameraSetup
import java.util.concurrent.TimeUnit


class AppPrefs {

    private val pref: SharedPreferences

    init {
        pref = PreferenceHelper.newPrefs(AppContext, SHARE_PREF_NAME)
    }

    companion object {
        private const val SHARE_PREF_NAME = "App.Pref"
        private const val KEY_TOKEN = "key.token"
        private const val KEY_TOKEN_SET_TIME = "key.token.set.time"
        private const val KEY_EMAIL = "key.email"
        private const val KEY_PASSWORD = "key.password"
        private const val KEY_USER_INFO = "key.user.info"
        private const val KEY_SETTING_MODEL = "key.setting.model"
        private const val KEY_LANGUAGE_KEY = "key.language.key"
        private const val KEY_COMMENT_VOICE = "key.comment.voice"
        private const val KEY_STYLE_OF_GALLERY = "key.style.of.gallery"
        private const val KEY_SECURITY = "key.security"

        @Volatile
        private var INSTANCE: AppPrefs? = null

        fun shared() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPrefs().also {
                    INSTANCE = it
                }
            }
    }

    fun clearData() {
        token = ""
    }

    var email: String?
        set(value) {
            pref[KEY_EMAIL] = value ?: ""
        }
        get() {
            return pref[KEY_EMAIL]
        }

    var password: String?
        set(value) {
            pref[KEY_PASSWORD] = value ?: ""
        }
        get() {
            return pref[KEY_PASSWORD]
        }

    var token: String?
        set(value) {
            pref[KEY_TOKEN] = value ?: ""
            pref[KEY_TOKEN_SET_TIME] = System.currentTimeMillis()
        }
        get() {
            return pref[KEY_TOKEN]
        }

    val tokenExpire: Boolean
        get() {
            val tokenTime = pref[KEY_TOKEN_SET_TIME, 0L] ?: 0
            return (System.currentTimeMillis() - tokenTime) > TimeUnit.DAYS.toMillis(Constant.TOKEN_TIME_OUT)
        }

    var userInfo: UserData?
        set(value) {
            value?.let {
                pref[KEY_USER_INFO] = it
            }
        }
        get() {
            return pref[KEY_USER_INFO]
        }
    var setting: SettingModel?
        set(value) {
            value?.let {
                pref[KEY_SETTING_MODEL] = it
                CameraSetup.Build(it.getStringLanguage())
            }
        }
        get() {
            val setting = SettingModel.newInstance()
            return pref[KEY_SETTING_MODEL, setting] ?: setting
        }

    var commentVoice: Int
        set(value) {
            pref[KEY_COMMENT_VOICE] = value
        }
        get() {
            return pref[KEY_COMMENT_VOICE, -1] ?: -1
        }


    var styleOfGallery: Boolean
        set(value) {
            pref[KEY_STYLE_OF_GALLERY] = value
        }
        get() {
            return pref[KEY_STYLE_OF_GALLERY, false] ?: false
        }

    var isSecurity: Boolean
        set(value) {
            pref[KEY_SECURITY] = value
        }
        get() {
            return pref[KEY_SECURITY, false] ?: false
        }
}