package com.app.album_maker.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.album_maker.R
import java.security.*
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


@RequiresApi(Build.VERSION_CODES.M)
class BiometricHelper(val context: Context, val fragment: Fragment, val callback: CallBack?) {
    //const
    private var biometricPrompt: BiometricPrompt
    private var promptBiometricInfo: PromptInfo
    private var promptInfo: PromptInfo
    private var biometricManager: BiometricManager = BiometricManager.from(context)
    private var keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
    private var executor: Executor = ContextCompat.getMainExecutor(context)

    init {
        biometricPrompt = createBiometricPrompt()
        promptBiometricInfo = createPromptInfoBiometric()
        promptInfo = createPromptInfo()
    }

    private fun generateSecretKey() {

        val keyGenBuilder = KeyGenParameterSpec.Builder(
            DEFAULT_KEY_NAME,PURPOSE_ENCRYPT or PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE_CBC)
            .setEncryptionPaddings(ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)

        // Invalidate the keys if the user has registered a new biometric
        // credential, such as a new fingerprint. Can call this method only
        // on Android 7.0 (API level 24) or higher. The variable
        // "invalidatedByBiometricEnrollment" is true by default.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            keyGenBuilder.setInvalidatedByBiometricEnrollment(true)
        }

        generateSecretKey(keyGenBuilder.build())
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ) {
                    // Because negative button says use application password
                    callback?.onCancelFinger()
                } else if (errorCode == BiometricPrompt.ERROR_LOCKOUT){
                    callback?.onFail(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed for an unknown reason")
                callback?.onFingerFail()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                callback?.onFingerPass()
            }
        }

        return BiometricPrompt(fragment, executor, callback)
    }

    private fun createPromptInfoBiometric(): PromptInfo {
        return PromptInfo.Builder()
            .setTitle(context.getString(R.string.prompt_info_title))
            .setDescription(context.getString(R.string.prompt_info_pin_description))
            .setConfirmationRequired(false)
            .setNegativeButtonText(context.getString(R.string.prompt_info_cancel))
            // .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
            // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
            // incompatible so that if you uncomment one you must comment out the other
            .build()
    }

    private fun createPromptInfo(): PromptInfo {
        return PromptInfo.Builder()
            .setTitle(context.getString(R.string.prompt_info_title))
            .setDescription(context.getString(R.string.prompt_info_description))
            .setConfirmationRequired(false)
            .setDeviceCredentialAllowed(true) // Allow PIN/pattern/password authentication.
            // Also note that setDeviceCredentialAllowed and setNegativeButtonText are
            // incompatible so that if you uncomment one you must comment out the other
            .build()
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(DEFAULT_KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KEY_ALGORITHM_AES + "/"
                + BLOCK_MODE_CBC + "/"
                + ENCRYPTION_PADDING_PKCS7
        )
    }

    fun actionBiometricLogin() {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                generateSecretKey()
                val cipher = getCipher()
                val secretKey = getSecretKey()
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                biometricPrompt.authenticate(promptBiometricInfo, BiometricPrompt.CryptoObject(cipher))
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                callback?.onFail("No biometric features available on this device.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                callback?.onFail("Biometric features are currently unavailable.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback?.onFail("The user hasn't associated " +
                        "any biometric credentials with their account.")
            }
        }
    }

    fun actionLogin() {
        if (keyguardManager?.isDeviceSecure == true) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            callback?.onFail("Biometric features are currently unavailable.")
        }
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val DEFAULT_KEY_NAME = "default.key"
        private const val TAG = "FingerHelper"
    }

    interface CallBack {
        fun onFingerPass()

        fun onFingerFail()

        fun onCancelFinger()

        fun onFail(message: String)
    }
}