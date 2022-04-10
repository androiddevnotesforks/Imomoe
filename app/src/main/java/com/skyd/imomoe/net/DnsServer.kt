package com.skyd.imomoe.net

import android.app.Activity
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.ext.showInputDialog
import com.skyd.imomoe.ext.showListDialog
import com.skyd.imomoe.util.showToast
import okhttp3.HttpUrl.Companion.toHttpUrl

object DnsServer {
    class Dns(val dnsName: String, val dnsServer: String) : CharSequence {
        override val length: Int
            get() = dnsServer.length

        override fun get(index: Int): Char = dnsServer[index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return dnsServer.subSequence(startIndex, endIndex)
        }

        override fun toString(): String =
            if (dnsServer.isBlank()) dnsName else "$dnsName: $dnsServer"

        override fun equals(other: Any?): Boolean {
            return when (other) {
                null -> false
                other === this -> true
                is String -> other == dnsServer
                is Dns -> other.dnsServer == this.dnsServer && other.dnsName == this.dnsName
                else -> false
            }
        }

        override fun hashCode(): Int {
            var result = dnsServer.hashCode()
            result = 31 * result + dnsName.hashCode()
            return result
        }
    }

    private infix fun String.to(that: String): Dns = Dns(this, that)

    val defaultDnsServer: List<Dns> = listOf(
        "不使用" to "",
        "alidns" to "https://223.5.5.5/dns-query",
        "Cloudflare" to "https://1.0.0.1/dns-query",
        "Google" to "https://8.8.8.8/dns-query"
    )

    var dnsServer: String? = null
        set(value) {
            if (value == null || value == field) return
            sharedPreferences().editor { putString("dnsServer", value) }
            field = value
            changeDnsServer(value)
        }
        get() {
            return field ?: sharedPreferences()
                .getString("dnsServer", null).apply { field = this }
        }

    fun Activity.selectDnsServer() {
        var initialSelection = -1
        defaultDnsServer.forEachIndexed { index, s ->
            if (s.equals(dnsServer)) initialSelection = index
        }
        if (dnsServer.isNullOrBlank()) initialSelection = 0
        showListDialog(
            title = getString(R.string.select_dns_server),
            items = defaultDnsServer,
            checkedItem = initialSelection,
            neutralText = getString(R.string.custom_dns_server),
            onNeutral = { dialog, _ ->
                customDnsServer()
                dialog.dismiss()
            }
        ) { _, _, itemIndex ->
            dnsServer = defaultDnsServer[itemIndex].dnsServer
        }
    }

    fun Activity.customDnsServer() {
        showInputDialog(
            title = getString(R.string.custom_dns_server_dialog_title),
            hint = getString(R.string.custom_dns_server_describe)
        ) { _, _, text ->
            val url = text.toString()
            runCatching {
                // 测试url合法性
                url.toHttpUrl()
                dnsServer = url
            }.onFailure { e ->
                e.printStackTrace()
                e.message?.showToast()
            }
        }
    }
}