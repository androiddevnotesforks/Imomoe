package com.skyd.imomoe.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.lang.Exception


enum class SwitchVideoMode {
    Once, RepeatOne, Next
}

var switchVideoMode: SwitchVideoMode
    set(value) {
        when (value) {
            SwitchVideoMode.Once -> {
                sharedPreferences().editor { putString("switchVideoMode", "Once") }
            }
            SwitchVideoMode.RepeatOne -> {
                sharedPreferences().editor { putString("switchVideoMode", "RepeatOne") }
            }
            SwitchVideoMode.Next -> {
                sharedPreferences().editor { putString("switchVideoMode", "Next") }
            }
        }
    }
    get() {
        return when (sharedPreferences().getString("switchVideoMode", "Once")) {
            "Once", "StopPlay" -> {
                SwitchVideoMode.Once
            }
            "RepeatOne" -> {
                SwitchVideoMode.RepeatOne
            }
            "Next", "AutoPlayNextEpisode" -> {
                SwitchVideoMode.Next
            }
            else -> {
                SwitchVideoMode.Once
            }
        }
    }

fun Context.getMediaTitle(uri: Uri): String? {
    return getMediaStringInfo(uri, MediaMetadataRetriever.METADATA_KEY_TITLE)
}

fun Context.getMediaMime(uri: Uri): String? {
    return getMediaStringInfo(uri, MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
}

fun Context.getMediaAlbumArt(uri: Uri): Bitmap? {
    val image: Bitmap?
    val mData = MediaMetadataRetriever()
    mData.setDataSource(this, uri)
    image = try {
        val art = mData.embeddedPicture!!
        BitmapFactory.decodeByteArray(art, 0, art.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    return image
}

private fun Context.getMediaStringInfo(uri: Uri, keyCode: Int): String? {
    val info: String?
    val mData = MediaMetadataRetriever()
    mData.setDataSource(this, uri)
    info = try {
        mData.extractMetadata(keyCode)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    return info
}

fun getMediaAlbumArt(path: String): Bitmap? {
    val image: Bitmap?
    val mData = MediaMetadataRetriever()
    mData.setDataSource(path)
    image = try {
        val art = mData.embeddedPicture!!
        BitmapFactory.decodeByteArray(art, 0, art.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    return image
}