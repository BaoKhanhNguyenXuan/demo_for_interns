package com.app.album_maker.base.voicerecord

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

class SpeechRecognitionListener(mListener: onSpeechResultsReady) : RecognitionListener {
    private val mListener: onSpeechResultsReady = mListener

    override fun onBeginningOfSpeech() {
        mListener.onBeginningOfSpeech()
    }

    override fun onBufferReceived(buffer: ByteArray) {}

    override fun onEndOfSpeech() {
        mListener.onEndOfSpeech()
    }

    @Synchronized
    override fun onError(error: Int) {
        mListener.onError(error)
    }

    override fun onEvent(eventType: Int, params: Bundle) {}

    override fun onPartialResults(partialResults: Bundle) {
        mListener?.onPartialResults(partialResults)
    }

    override fun onReadyForSpeech(params: Bundle) {
    }

    override fun onResults(results: Bundle?) {
        if (results != null && mListener != null) {
            val ahihi = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            mListener.onResults(ahihi)
        }
    }

    override fun onRmsChanged(rmsdB: Float) {
        mListener.onRmsChanged(rmsdB)
    }
}