package com.skyd.imomoe.ext

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.skyd.imomoe.appContext


fun Context.sharedPreferences(name: String = "App"): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)

fun SharedPreferences.editor(editorBuilder: SharedPreferences.Editor.() -> Unit) =
    edit().apply(editorBuilder).apply()

fun SharedPreferences.editor2(editorBuilder: SharedPreferences.Editor.() -> Unit) =
    edit().apply(editorBuilder).commit()

fun sharedPreferences(name: String = "App"): SharedPreferences = appContext.sharedPreferences(name)

@RequiresApi(Build.VERSION_CODES.M)
fun secretSharedPreferences(name: String = "Secret"): SharedPreferences {
    val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    return EncryptedSharedPreferences.create(
        appContext,
        name,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
