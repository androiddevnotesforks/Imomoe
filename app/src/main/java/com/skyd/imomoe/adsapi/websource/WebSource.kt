package com.skyd.imomoe.adsapi.websource

import com.skyd.imomoe.util.html.source.WebSource

object WebSource {
    suspend fun getWebSource(
        url: String,
        encoding: String = "UTF-8",
        userAgent: String? = null,
        timeout: Long = 10000L
    ): String {
        return WebSource.getWebSource(
            url = url,
            encoding = encoding,
            userAgent = userAgent,
            timeout = timeout
        )
    }
}
