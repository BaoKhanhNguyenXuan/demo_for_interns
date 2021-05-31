package com.app.album_maker.data.model.response

import com.app.album_maker.data.apis.response.BaseResponse
import com.app.album_maker.data.model.SettingModel
import com.app.album_maker.data.model.UserData
import com.google.gson.annotations.SerializedName

class LoginResponse(
        @SerializedName("data") val data: LoginResponseData
) : BaseResponse()

class  LoginResponseData(
        @SerializedName("user_token") val token: String,
        @SerializedName("user_data") val userInfo: UserData,
        @SerializedName("userSetting") val userSetting: SettingModel
)