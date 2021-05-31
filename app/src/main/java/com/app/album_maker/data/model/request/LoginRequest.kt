package com.app.album_maker.data.model.request

import com.google.gson.annotations.SerializedName

class LoginRequest(
    @SerializedName("company_code") val companyCode: String = "sotdmy",
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("os") val os: Int = 2,
    @SerializedName("device_token") val device_token: String = ""
)