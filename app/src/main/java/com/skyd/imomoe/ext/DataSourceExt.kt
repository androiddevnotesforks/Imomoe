package com.skyd.imomoe.ext

import kotlinx.coroutines.flow.MutableSharedFlow

var dataSourceDirectoryChanged: MutableSharedFlow<Boolean> =
    MutableSharedFlow(extraBufferCapacity = 1)
