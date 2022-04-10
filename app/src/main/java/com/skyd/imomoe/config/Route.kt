package com.skyd.imomoe.config

import android.net.Uri

object Route {
    const val SCHEME = "anime"
    val ROUTE_OPEN_APP = Uri.parse("$SCHEME://open.anime.app")
    val ROUTE_NAV = Uri.parse("$SCHEME://navi.anime.app")
    val ROUTE_JUMP_BY_URL = Uri.parse("$SCHEME://jumpByUrl.anime.app")
}