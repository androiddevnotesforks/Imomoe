package com.skyd.imomoe.util.download

interface DownloadListener {
    fun complete(fileName: String)

    fun error() {
    }
}