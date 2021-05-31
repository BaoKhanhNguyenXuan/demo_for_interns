package com.app.album_maker.data.model

import android.os.Parcelable
import com.app.album_maker.utils.Constant.DEFAULT_LIMIT_IMAGES_UPLOAD
import com.app.album_maker.utils.Constant.IMAGE_SIZE_1MB
import com.app.album_maker.utils.Constant.JAPANESE
import com.app.album_maker.utils.Constant.VOICE_COMMENT_ON
import com.app.album_maker.utils.SettingOption
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingModel(
    @SerializedName("language") var language: Int,
    @SerializedName("voice_comment") var voice_comment: Int,
    @SerializedName("image_size") var image_size: Int,
    @SerializedName("upload_limit") var uploadLimit: Int
) : Parcelable {

    companion object {
        fun newInstance() = SettingModel(JAPANESE, VOICE_COMMENT_ON, IMAGE_SIZE_1MB, DEFAULT_LIMIT_IMAGES_UPLOAD)
    }

    override fun equals(other: Any?): Boolean {
        if ((other is SettingModel) && (this.language == other.language) && (this.voice_comment == other.voice_comment) && (this.image_size == other.image_size) && (this.uploadLimit == other.uploadLimit)) {
            return true
        }
        return super.equals(other)
    }

    fun getStringLanguage(): String? {
        return when (language) {
            1 -> SettingOption.JAPANESE
            2 -> SettingOption.VIETNAMESE
            3 -> SettingOption.ENGLISH
            else -> null
        }
    }
}

