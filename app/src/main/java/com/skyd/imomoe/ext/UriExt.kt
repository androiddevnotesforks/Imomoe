package com.skyd.imomoe.ext

import android.net.Uri
import com.skyd.imomoe.appContext
import java.io.File
import java.io.FileOutputStream


fun Uri.copyTo(target: File): File {
    appContext.contentResolver.openInputStream(this)!!.copyTo(FileOutputStream(target))
    return target
}
