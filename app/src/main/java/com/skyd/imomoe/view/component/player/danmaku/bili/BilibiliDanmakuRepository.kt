package com.skyd.imomoe.view.component.player.danmaku.bili

import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.skyd.imomoe.ext.string
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.util.Util.toEncodedUrl
import com.skyd.imomoe.view.component.player.danmaku.DanmakuRepository
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.helpers.XMLReaderFactory
import java.io.IOException
import java.util.*
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

class BilibiliDanmakuRepository(val url: String) : DanmakuRepository {
    init {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver")
    }

    override suspend fun parse(): List<DanmakuItemData> {
        val inputStream = InflaterInputStream(
            RetrofitManager.get().create(DanmakuService::class.java)
                .getCustomizeDanmaku(url.toEncodedUrl()).byteStream(),
            Inflater(true)
        )
        val data: String = inputStream.string()
        if (data.isBlank()) return ArrayList()
        try {
            val xmlReader = XMLReaderFactory.createXMLReader()
            val contentHandler = XmlContentHandler()
            xmlReader.contentHandler = contentHandler
            xmlReader.parse(InputSource(data.byteInputStream()))
            return contentHandler.result
        } catch (e: SAXException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    class XmlContentHandler : DefaultHandler() {
        lateinit var result: MutableList<DanmakuItemData>
        var item: DanmakuItemData? = null
        var completed = false

        override fun startDocument() {
            result = ArrayList()
        }

        override fun endDocument() {
            completed = true
        }

        override fun startElement(
            uri: String,
            localName: String,
            qName: String,
            attributes: Attributes
        ) {
            var tagName = if (localName.isNotEmpty()) localName else qName
            tagName = tagName.lowercase(Locale.getDefault()).trim { it <= ' ' }
            if (tagName == "d") {
                // <d p="23.826000213623,1,25,16777215,1422201084,0,057075e9,757076900">我从未见过如此厚颜无耻之猴</d>
                // 0:时间(弹幕出现时间)
                // 1:类型(1从右至左滚动弹幕|6从左至右滚动弹幕|5顶端固定弹幕|4底端固定弹幕|7高级弹幕|8脚本弹幕)
                // 2:字号
                // 3:颜色
                // 4:时间戳 ?
                // 5:弹幕池id
                // 6:用户hash
                // 7:弹幕id
                val pValue = attributes.getValue("p")
                // parse p value to danmaku
                val values = pValue.split(",").toTypedArray()

                if (values.isNotEmpty()) {
                    try {
                        item = DanmakuItemData(
                            content = "",
                            danmakuId = values[7].toLong(),
                            textSize = getTextSize(values[2].toInt()).toInt(),
                            textColor = values[3].toInt(),
                            position = (values[0].toFloat() * 1000).toLong(),
                            mode = getType(values[1].toInt())
                        )
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            item?.let {
                val tagName = if (localName.isNotEmpty()) localName else qName
                if (tagName.equals("d", ignoreCase = true)) {
                    result.add(it)
                }
                item = null
            }
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            item?.let {
                item = DanmakuItemData(
                    content = decodeXmlString(String(ch, start, length)),
                    danmakuId = it.danmakuId,
                    textSize = it.textSize,
                    textColor = it.textColor,
                    position = it.position,
                    mode = it.mode
                )
            }
        }

        private fun decodeXmlString(s: String): String {
            var result = s
            if (result.contains("&amp;")) {
                result = result.replace("&amp;", "&")
            }
            if (result.contains("&quot;")) {
                result = result.replace("&quot;", "\"")
            }
            if (result.contains("&gt;")) {
                result = result.replace("&gt;", ">")
            }
            if (result.contains("&lt;")) {
                result = result.replace("&lt;", "<")
            }
            return result
        }

        companion object {
            /**
             * 获取真实的字体大小px
             * 弹幕库限制字体最小12f最大25f
             * @param n 12非常小,16特小,18小,25中,36大,45很大,64特别大
             * @return 以px为单位的字体大小
             */
            private fun getTextSize(n: Int): Float {
                return when (n) {
                    12 -> 12f
                    16 -> 14f
                    18 -> 17f
                    25 -> 19f
                    36 -> 21f
                    45 -> 23f
                    64 -> 25f
                    else -> 19f
                }
            }

            private fun getType(s: Int): Int {
                // 类型(1从右至左滚动弹幕|6从左至右滚动弹幕|5顶端固定弹幕|4底端固定弹幕|7高级弹幕|8脚本弹幕)
                return when (s) {
                    1 -> DanmakuItemData.DANMAKU_MODE_ROLLING
                    4 -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    5 -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    else -> DanmakuItemData.DANMAKU_MODE_ROLLING
                }
            }
        }
    }
}