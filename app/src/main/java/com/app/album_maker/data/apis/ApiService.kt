package com.app.album_maker.data.apis

import com.app.album_maker.data.apis.response.BaseResponse
import com.app.album_maker.data.model.request.LoginRequest
import com.app.album_maker.data.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    //Authentication
    @POST("/api/v2/app/user/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("logout")
    fun logOut(): Call<BaseResponse>
    //Authentication - end

}