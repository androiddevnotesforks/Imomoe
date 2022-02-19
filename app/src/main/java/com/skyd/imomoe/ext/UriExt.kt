package com.skyd.imomoe.ext

import android.net.Uri
import com.skyd.imomoe.App
import java.io.File
import java.io.FileOutputStream


fun Uri.copyTo(target: File): File {
    App.context.contentResolver.openInputStream(this)!!.copyTo(FileOutputStream(target))
    return target
}
