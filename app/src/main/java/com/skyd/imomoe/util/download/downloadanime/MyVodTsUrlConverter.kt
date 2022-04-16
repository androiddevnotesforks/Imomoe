package com.skyd.imomoe.util.download.downloadanime

import com.arialyy.aria.core.processor.IVodTsUrlConverter
import java.net.MalformedURLException
import java.net.URL

/**
 * m3u8获取ts地址
 */
class MyVodTsUrlConverter : IVodTsUrlConverter {
    override fun convert(m3u8Url: String, tsUrls: List<String>): List<String> {
        val index = m3u8Url.lastIndexOf("/")
        val parentUrl = m3u8Url.substring(0, index + 1)
        val newUrls: MutableList<String> = ArrayList()
        for (urls in tsUrls) {
            if (urls.startsWith("http")) {
                newUrls.add(urls)
            } else {
                val temp = parentUrl + urls
                try {
                    val url = URL(temp)
                    val host: String = url.host
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
                    newUrls.add(stringBuilder.toString())
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }
        return newUrls
    }
}