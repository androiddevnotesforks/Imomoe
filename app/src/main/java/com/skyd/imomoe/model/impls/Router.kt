package com.skyd.imomoe.model.impls

import android.content.Context
import android.net.Uri
import com.skyd.imomoe.model.interfaces.IRouter

class Router : IRouter {
    override fun route(uri: Uri, context: Context?): Boolean = false
}
