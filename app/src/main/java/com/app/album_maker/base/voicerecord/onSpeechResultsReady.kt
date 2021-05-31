package com.app.album_maker.base.voicerecord

import android.os.Bundle

interface onSpeechResultsReady {
    fun onPartialResults(partialResults: Bundle)

    fun onError(error: Int)

    // Trả về dữ liệu khi hoàn thành nhận dạng hoặc gặp lỗi
    fun onResults(results: ArrayList<String>?)

    // Trả về dữ liệu mỗi khi chúng ta nói
    fun onRmsChanged(rmsdB: Float)

    fun onBeginningOfSpeech()

    fun onEndOfSpeech()
}