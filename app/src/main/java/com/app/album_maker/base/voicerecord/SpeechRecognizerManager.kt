package com.app.album_maker.base.voicerecord

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.app.album_maker.utils.AppPrefs
import com.app.album_maker.utils.SettingOption

class SpeechRecognizerManager(val context: Context, val mListener: onSpeechResultsReady) {
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private var mSpeechRecognizerIntent: Intent
    private var language = SettingOption.ENGLISH
    private var timeout = 2000L // 2000 ms
    private var countDownTimer: CountDownTimer? = null

    init {
        when (AppPrefs.shared().setting?.language) {
            1 -> language = SettingOption.SPEECH_JAPAN
            2 -> language = SettingOption.SPEECH_VIETNAM
            3 -> language = SettingOption.ENGLISH
            else -> language = SettingOption.SPEECH_JAPAN
        }

        // Create new intent
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            context.getPackageName()
        )
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language)
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
            language
        )
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        ) // For streaming result
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            timeout
        )
    }

    fun resetSpeechRecognizer() {
        destroy()
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    fun startListening() {
        mSpeechRecognizer?.let {
            it.startListening(mSpeechRecognizerIntent)
//            countDownTimer?.cancel()
//            countDownTimer = object : CountDownTimer(29000, 1000) {
//                override fun onTick(millisUntilFinished: Long) {
//                }
//
//                override fun onFinish() {
//                }
//            }
//            (countDownTimer as CountDownTimer).start()
        }
    }


    fun setRecognitionListener() {
        mSpeechRecognizer?.let { it.setRecognitionListener(
            SpeechRecognitionListener(
                mListener
            )
        ) }
    }

    fun stopListening() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer!!.stopListening()
        }
        countDownTimer?.cancel()
    }

    fun destroy() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer!!.stopListening()
            mSpeechRecognizer!!.cancel()
            mSpeechRecognizer!!.destroy()
            mSpeechRecognizer = null
        }
        countDownTimer?.cancel()
    }
}