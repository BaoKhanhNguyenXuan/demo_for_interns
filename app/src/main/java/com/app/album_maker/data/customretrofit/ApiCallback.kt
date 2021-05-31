package com.app.album_maker.data.customretrofit

import com.app.album_maker.data.apis.response.BaseResponse
import com.app.album_maker.utils.HttpCode
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class  ApiCallback<T> : Callback<T> {

    abstract fun onResponse(response: T)

    abstract fun onFailure(t: Throwable)

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val type = getGenericType()
        val body = when (val code = response.code()) {
            in HttpCode.RESULT_OK..299 -> {
                response.body() ?: run {
                    val temp = BaseResponse()
                    temp.code = code
                    temp.message = response.message()
                    val bodyString = Gson().toJson(temp)
                    Gson().fromJson<T>(bodyString, type)
                }
            }
            else -> {
                response.errorBody()?.let {
                    val bodyString = it.string()
                    Gson().fromJson<T>(bodyString, type)
                }?: kotlin.run {
                    val temp = BaseResponse()
                    temp.code = code
                    temp.message = response.message()
                    val bodyString = Gson().toJson(temp)
                    Gson().fromJson<T>(bodyString, type)
                }
            }
        }
        onResponse(body)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure(t)
    }

    private fun getGenericType(): Type {
        val classType =  javaClass.genericSuperclass ?: javaClass.genericInterfaces
        if (classType is ParameterizedType) {
            if (classType.actualTypeArguments.isNotEmpty()) {
                return classType.actualTypeArguments[0]
            }
        }
        return javaClass
    }
}