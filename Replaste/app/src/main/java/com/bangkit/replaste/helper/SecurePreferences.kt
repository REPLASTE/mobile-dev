package com.bangkit.replaste.helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecurePreferences(context: Context) {
    companion object {
        private const val PREF_NAME = "secure_prefs"
        private const val JWT_TOKEN_KEY = "jwt_token"
    }

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveJwtToken(token: String) {
        val encryptedToken = Base64.encodeToString(token.toByteArray(), Base64.DEFAULT)
        sharedPreferences.edit().putString(JWT_TOKEN_KEY, encryptedToken).apply()
    }

    fun getJwtToken(): String? {
        val encryptedToken = sharedPreferences.getString(JWT_TOKEN_KEY, null)
        return encryptedToken?.let {
            String(Base64.decode(it, Base64.DEFAULT))
        }
    }

    fun clearJwtToken() {
        sharedPreferences.edit().remove(JWT_TOKEN_KEY).apply()
    }
}
