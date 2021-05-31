package com.app.album_maker.utils

import android.Manifest
import android.graphics.Color
import com.app.album_maker.MyApp

object Constant {
    const val TOKEN_TIME_OUT: Long = 3
    const val HOUSE: Int = 2
    const val APARTMENT: Int = 1

    const val GALLERY_IMAGE = 101
    const val CAMERA = 102
    const val PREVIEW = 103
    const val PHOTO_EDITOR = 104
    const val RECORDING_VIDEO = 105
    const val GALLERY_VIDEO = 106

    const val DETAIL_ALBUM = 1
    const val NEW_HOUSE_DETAIL = 2

    const val JAPANESE = 1
    const val VIETNAMESE = 2
    const val ENGLISH = 3
    const val IMAGE_SIZE_1MB = 1
    const val IMAGE_SIZE_2MB = 2
    const val IMAGE_SIZE_3MB = 3
    const val VOICE_COMMENT_ON = 1
    const val VOICE_COMMENT_OFF = 0
    const val DEFAULT_LIMIT_IMAGES_UPLOAD = 10

    const val CAMERA_RESULT = "CAMERA RESULT"
    //const val GALLERY_RESULT = "GALLERY RESULT"
    const val PREVIEW_RESULT = "PREVIEW_RESULT"
    const val PHOTO_EDITOR_RESULT = "PHOTO_EDITOR_RESULT"

    const val IMAGE_UPLOAD_SUCCESS = 1
    const val IMAGE_UPLOAD_FAIL = 0

    const val FRONT_CAMERA = 0
    const val BACK_CAMERA = 1

    const val VIEW_ALBUM = 1
    const val CREATE_ALBUM = 2

    const val CONNECT_TIME_OUT = 30L
    const val MAX_LENGTH_COMMENT = 255
    const val MAX_COMMENT_LINE = 2
    const val SEE_MORE_COLOR = Color.LTGRAY
    const val FREE_SPACE_NEED = 50
    const val MAX_BYTE: Int = 1024 * 1024
    const val CODE_RESPONSE_ONFAILURE = -1
    const val DELETE_IMAGE = -11

    const val STYLE_OF_GALLERY_KEY = "STYLE_OF_GALLERY_KEY"

    const val UPLOAD_TYPE_IMAGE = 1
    const val UPLOAD_TYPE_VIDEO = 2
    const val VIDEO_MAX_TIME = 10
    const val DEFAULT_LIMIT_VIDEO_UPLOAD = 1
}

object SettingOption {

    //  LANGUAGE
    const val ENGLISH = "en"
    const val JAPANESE = "ja"
    const val VIETNAMESE = "vi"

    const val SPEECH_JAPAN = "ja-JP"
    const val SPEECH_VIETNAM = "vi-VN"
    const val SHOW_MIC = 1
}

object Strings {
    const val INTERNAL_SERVER_ERROR = "Internal server error"
    const val SERVICE_UNAVAILABLE = "Services unavailable. Please check your network connection!"
    const val SERVICE_TIMEOUT = "request timeout"
    const val CLIENT_UNKNOWN_ERROR = "client unknown error"
    const val HOUSE_ID = "HOUSE_ID"
    const val IMAGE_ID = "IMAGE_ID"
    const val TIME_FORMAT = "yyyyMMdd_HHmmss"
    const val ROOM_ID = "RoomID"
    const val POSITION_ROOM = "POSITION_ROOM"
    const val POSITION_IMAGE = "POSITION_IMAGE"
    const val THUMBNAIL = "THUMBNAIL"
    const val STATUS = "STATUS"
    const val LIST_PICTURE = "LIST_PICTURE"
    const val HOUSE_INFO = "HOUSE_INFO"
    const val IMAGES_CAPTURE = "IMAGES_CAPTURE"
    const val DELETE_IMAGE = "DELETE_IMAGE"
    const val VIEW_IMAGE = "VIEW_IMAGE"
    const val ROOM_NAME = "ROOM_NAME"
    const val PATH = "PATH"
    const val IS_ADD_VIDEO = "IS_ADD_VIDEO"

}

object HttpCode {
    const val RESULT_OK = 200
    const val UNKNOWN_ERROR = 260
    const val SERVICE_UNAVAILABLE = 261
    const val SERVICE_TIMEOUT = 262
    const val CLIENT_CATCH_ERROR = 263
    const val SYSTEM_ERROR = 400
    const val FORBIDDEN = 403
    const val UNAUTHORIZED = 401
    const val NOT_FOUND = 404
    const val UNPROCESSABLE_ENTITYED = 422
    const val INTERNAL_SERVER_ERROR = 500
}

object Permission {
    val PERMISSION_REQUEST = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    const val REQUEST_START = 0
    const val REQUEST_ALL_ALBUM = 1
    const val REQUEST_CREATE_ALBUM = 2
    const val REQUEST_CAMERA = 3
}

val AppContext = MyApp.get()