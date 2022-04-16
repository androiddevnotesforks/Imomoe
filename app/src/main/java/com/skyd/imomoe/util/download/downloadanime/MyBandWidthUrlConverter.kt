package com.skyd.imomoe.util.download.downloadanime

import com.arialyy.aria.core.processor.IBandWidthUrlConverter
import java.net.MalformedURLException
import java.net.URL


class MyBandWidthUrlConverter : IBandWidthUrlConverter {
    override fun convert(m3u8Url: String, bandWidthUrl: String): String {
        val index = m3u8Url.lastIndexOf("/")
        val parentUrl = m3u8Url.substring(0, index + 1)
        return if (bandWidthUrl.startsWith("http")) {
            bandWidthUrl
        } else {
            val temp = parentUrl + bandWidthUrl
            try {
                val url = URL(temp)
                val host = url.host
                val indexHost = temp.indexOf(host, 0)
                val hosts = temp.substring(0, indexHost + host.length)
                val newTemp = temp.substring(hosts.length)
                val strings = newTemp.split("/").toTypedArray()
                val stringBuilder = StringBuilder(hosts)
                for (str in strings) {
                    if (str.isNotEmpty()) {
                        if (!stringBuilder.toString().contains(str)) {
                            stringBuilder.append("/")
                            stringBuilder.append(str)
                        }
                    }
                }
                return stringBuilder.toString()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
            temp
        }
    }
}