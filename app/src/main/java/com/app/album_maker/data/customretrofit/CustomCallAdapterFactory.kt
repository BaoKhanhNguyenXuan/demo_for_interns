package com.app.album_maker.data.customretrofit

import com.app.album_maker.data.apis.response.BaseResponse
import com.app.album_maker.utils.HttpCode
import com.app.album_maker.utils.Strings
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


class CustomCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        if (rawType != Call::class.java) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                " return type must be parameterized"
                        + " as " + "<Foo> or " + "<? extends Foo>"
            )
        }

        val responseType = getParameterUpperBound(0, returnType)

        return object : CallAdapter<Any, Call<*>> {
            override fun adapt(call: Call<Any>): Call<*> {
                return MyCall(call, responseType)
            }

            override fun responseType(): Type {
                return responseType
            }

        }
    }

    private class MyCall<T>(private val call: Call<T>, private val type: Type) : Call<T> {
        private var delegate: Call<T> = call
        private var disposable: Disposable? = null

        override fun enqueue(callback: Callback<T>) {
            disposable = Single.just(callback)
                .map {
                    execute()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { response ->
                    publishData(response, callback)
                }

        }

        private fun publishData(response: Response<T>, callback: Callback<T>) {
            try {
                callback.onResponse(call, response)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(call, Throwable(e.message))
            }
        }

        override fun isExecuted(): Boolean {
            return delegate.isExecuted
        }

        override fun clone(): Call<T> {
            return MyCall<T>(delegate.clone(), type)
        }

        override fun isCanceled(): Boolean {
            return delegate.isCanceled
        }

        override fun cancel() {
            delegate.cancel()
            disposable?.dispose()
        }

        override fun execute(): Response<T> {
            try {
                return delegate.execute()
            } catch (e: Exception) {
                e.printStackTrace()
                val response = BaseResponse()
                val code: Int
                val message: String
                when (e) {
                    is UnknownHostException,
                    is ConnectException -> {
                        code = HttpCode.SERVICE_UNAVAILABLE
                        message = Strings.SERVICE_UNAVAILABLE
                    }
                    is TimeoutException -> {
                        code = HttpCode.SERVICE_TIMEOUT
                        message = Strings.SERVICE_TIMEOUT
                    }
                    else -> {
                        code = HttpCode.UNKNOWN_ERROR
                        message = e.message ?: Strings.CLIENT_UNKNOWN_ERROR
                    }
                }
                //No internet connection
                response.code = code
                response.message = message
                val bodyString = Gson().toJson(response)
                val body = Gson().fromJson<T>(bodyString, type)
                return Response.success(code, body)
            }
        }

        override fun request(): Request {
            return delegate.request()
        }
    }
}