package com.skyd.imomoe.route.processor

import android.content.Context
import android.net.Uri

abstract class Processor {
    abstract val route: String
    abstract fun process(uri: Uri, context: Context?)
}