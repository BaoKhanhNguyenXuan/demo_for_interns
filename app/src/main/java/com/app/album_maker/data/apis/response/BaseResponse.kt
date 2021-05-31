package com.app.album_maker.data.apis.response

import com.app.album_maker.utils.HttpCode
import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("code")
    var code: Int = HttpCode.UNKNOWN_ERROR
    @SerializedName("message")
    var message: String? = null
}