package com.app.album_maker.data.apis

import com.app.album_maker.BuildConfig
import com.app.album_maker.data.customretrofit.CustomCallAdapterFactory
import com.app.album_maker.utils.AppPrefs
import com.app.album_maker.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    val BASE_URL = BuildConfig.SERVER_URL

    private var INSTANCE: ApiService? = null
    private fun getRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CustomCallAdapterFactory())
            .client(getUnsafeHttpClient())
            .build()
    }

    fun getApiService(): ApiService {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: getRetrofitClient().create(ApiService::class.java).also {
                INSTANCE = it
            }
        }
    }
}

fun getUnsafeHttpClient(): OkHttpClient {
    val bodyLog = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    return OkHttpClient.Builder()
        .connectTimeout(Constant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(Constant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(Constant.CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .sslSocketFactory(getSSLContext().socketFactory, trustAllCerts[0] as X509TrustManager)
        .addInterceptor(bodyLog)
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader(
                    "token", AppPrefs.shared().token ?: ""// Need add token
                )
                .build()
            it.proceed(request)
        }
        .build()
}

fun getSSLContext(): SSLContext {
    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    return sslContext
}

val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
    override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, authType: String?) {
    }

    override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, authType: String?) {
    }

    fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>, authType: String, host: String) {

    }

    override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? {
        return emptyArray()
    }
})