package com.app.album_maker.ui.authentication

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.app.album_maker.R
import com.app.album_maker.base.BaseActivity
import com.app.album_maker.data.apis.ApiClient
import com.app.album_maker.data.customretrofit.ApiCallback
import com.app.album_maker.data.model.request.LoginRequest
import com.app.album_maker.data.model.response.LoginResponse
import com.app.album_maker.utils.AppPrefs
import com.app.album_maker.utils.HttpCode

class AuthenticationActivity : BaseActivity() {

    override val layoutResId: Int
        get() = R.layout.activity_authentication

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Toolbar>(R.id.toolbar)?.let {
            setSupportActionBar(it)
        }
        autoLogin()
    }

    private fun autoLogin() {
        val request = LoginRequest(email = "hung.tran@supenient.vn", password =  "123456")
        ApiClient.getApiService().login(request).enqueue(object : ApiCallback<LoginResponse>() {
            override fun onResponse(response: LoginResponse) {
                when (response.code) {
                    HttpCode.RESULT_OK -> {
                        AppPrefs.shared().run {
                            this.token = response.data.token
                            this.email = request.email
                            this.password = request.password
                            this.userInfo = response.data.userInfo
                            this.setting = response.data.userSetting
                        }
                    }
                    else -> {
                        //todo
                    }
                }
            }

            override fun onFailure(t: Throwable) {
                //todo
            }
        })
    }

    override fun setupView() {

    }

    override fun onSupportNavigateUp(): Boolean {
        return false
    }
}
