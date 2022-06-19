package com.skyd.imomoe.ext

import android.content.Context
import androidx.annotation.RawRes
import com.skyd.imomoe.R
import java.io.*
import java.nio.charset.Charset


fun InputStream.string(charset: Charset = Charsets.UTF_8): String {
    val outputStream = ByteArrayOutputStream()
    var len: Int
    val buffer = ByteArray(1024)
    while (read(buffer).also { len = it } != -1) {
        outputStream.write(buffer, 0, len)
    }
    close()
    outputStream.close()
    return String(outputStream.toByteArray(), charset)
}

fun Context.getRawString(@RawRes id: Int, charset: Charset = Charsets.UTF_8): String {
    val sb = StringBuffer()
    try {
        val inputStream = resources.openRawResource(id)
        val reader = BufferedReader(InputStreamReader(inputStream, charset))
        var out: String?
        while (reader.readLine().also { out = it } != null) {
            sb.append(out)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return sb.toString()
}