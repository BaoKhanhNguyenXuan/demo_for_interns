package com.app.album_maker.utils

import android.util.Base64
import java.io.File

class Base64Helper {
    fun enCode(filePath: String):String{
        val bytes = File(filePath).readBytes()
        return Base64.encode(bytes, Base64.DEFAULT).toString()
    }
    fun deCode(base64String: String, filePath: String){
        File(filePath).writeBytes(Base64.decode(base64String, Base64.DEFAULT))
    }
}