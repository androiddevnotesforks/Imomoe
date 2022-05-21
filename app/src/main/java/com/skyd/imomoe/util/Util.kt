package com.skyd.imomoe.util

import android.app.Activity
import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.model.DataSourceManager
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Util {

    fun openBrowser(url: String) {
        try {
            val uri: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            appContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            appContext.getString(R.string.no_browser_found, url).showToast(Toast.LENGTH_LONG)
        }
    }

    fun String.toEncodedUrl(): String {
        return Uri.encode(this, ":/-![].,%?&=")
    }

    @Deprecated(
        "use String.toEncodedUrl()",
        ReplaceWith("url.toEncodedUrl()", "com.skyd.imomoe.util.Util.toEncodedUrl")
    )
    fun getEncodedUrl(url: String): String = url.toEncodedUrl()

    fun restartApp() {
        val i = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)
        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        appContext.startActivity(i)

        // 杀死原进程
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * 上次读过的用户须知的版本号
     */
    fun lastReadUserNoticeVersion(): Int = sharedPreferences().getInt("userNotice", 0)

    /**
     * @param version 用户须知版本号
     */
    fun setReadUserNoticeVersion(version: Int) = sharedPreferences().editor {
        putInt("userNotice", version)
    }

    /**
     * 获取用户须知String
     */
    fun getUserNoticeContent(): String {
        val sb = StringBuffer()
        try {
            val inputStream = appContext.resources.openRawResource(R.raw.notice)
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var out: String?
            while (reader.readLine().also { out = it } != null) {
                sb.append(out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    fun getWebsiteLinkSuffix(): String {
        return sharedPreferences().getString("websiteLinkSuffix", ".html") ?: ".html"
    }

    fun setWebsiteLinkSuffix(suffix: String) {
        sharedPreferences().editor { putString("websiteLinkSuffix", suffix) }
    }

    fun openVideoByExternalPlayer(context: Context, url: String): Boolean {
        return try {
            val uri: Uri =
                if (url.startsWith("file:///")) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        FileProvider.getUriForFile(
                            context,
                            "${context.applicationInfo.packageName}.fileProvider",
                            File(
                                url.substring(0, url.lastIndexOf("/")).replaceFirst("file:///", ""),
                                url.substring(url.lastIndexOf("/") + 1, url.length)
                            )
                        )
                    } else {
                        Uri.parse(url)
                    }
                } else Uri.parse(url)

            Intent().setAction(Intent.ACTION_VIEW).addFlags(FLAG_ACTIVITY_NEW_TASK)
                .addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "video/*").apply {
                    context.startActivity(
                        Intent.createChooser(
                            this, context.getString(R.string.choose_video_player)
                        )
                    )
                }
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }


    /**
     * 由于SUNDAY == 1...，因此需要转换成SUNDAY == 7...
     * @param day Calendar中的日期
     */
    fun getRealDayOfWeek(day: Int) = if (day == 1) 7 else day - 1

    /**
     * 返回星期几
     * @param day Calendar中的日期
     */
    fun getWeekday(day: Int): String {
        return if (day == 1) "星期天" else when (day - 1) {
            1 -> "星期一"
            2 -> "星期二"
            3 -> "星期三"
            4 -> "星期四"
            5 -> "星期五"
            else -> "星期六"
        }
    }

    /**
     * 获取系统屏幕亮度
     */
    fun getScreenBrightness(activity: Activity): Int? = try {
        Settings.System.getInt(activity.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
        null
    }

    /**
     * 获取重定向最终的地址
     * @param path
     */
    fun getRedirectUrl(path: String, referer: String = ""): String {
        var url = path
        return try {
            var conn: HttpURLConnection
            do {
                conn = URL(url).openConnection() as HttpURLConnection
                conn.setRequestProperty("Referer", referer)
                conn.headerFields
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 5000
                conn.getHeaderField("Location")?.let {
                    url = conn.getHeaderField("Location")
                }
                conn.disconnect()
            } while (conn.responseCode == 302 && conn.getHeaderField("Location") != null)
            url
        } catch (e: IOException) {
            e.printStackTrace()
            url
        }
    }

    /**
     * 通过id获取drawable
     */
    fun getResDrawable(@DrawableRes id: Int) = AppCompatResources.getDrawable(appContext, id)

    /**
     * 通过id获取颜色
     */
    fun getResColor(@ColorRes id: Int) = ContextCompat.getColor(appContext, id)

    /**
     * 计算距今时间
     * @param timeStamp 过去的时间戳
     */
    fun time2Now(timeStamp: Long): String {
        val nowTimeStamp = System.currentTimeMillis()
        var result = "非法输入"
        val dateDiff = nowTimeStamp - timeStamp
        if (dateDiff >= 0) {
            val bef = Calendar.getInstance().apply { time = Date(timeStamp) }
            val aft = Calendar.getInstance().apply { time = Date(nowTimeStamp) }
            val second = dateDiff / 1000.0
            val minute = second / 60.0
            val hour = minute / 60.0
            val day = hour / 24.0
            val month =
                aft[Calendar.MONTH] - bef[Calendar.MONTH] + (aft[Calendar.YEAR] - bef[Calendar.YEAR]) * 12
            val year = month / 12.0
            result = when {
                year.toInt() > 0 -> "${year.toInt()}年前"
                month > 0 -> "${month}个月前"
                day.toInt() > 0 -> "${day.toInt()}天前"
                hour.toInt() > 0 -> "${hour.toInt()}小时前"
                minute.toInt() > 0 -> "${minute.toInt()}分钟前"
                else -> "刚刚"
            }
        }
        return result
    }

    fun String.copy2Clipboard(context: Context) {
        try {
            val systemService: ClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            systemService.setPrimaryClip(ClipData.newPlainText("text", this))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNewVersionByVersionCode(version: String): Boolean {
        val currentVersion = getAppVersionCode().toString()
        return try {
            version != currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            "检查版本号失败，建议手动到GitHub查看是否有更新\n当前版本代码：$currentVersion".showToast(Toast.LENGTH_LONG)
            false
        }
    }

    fun getAppVersionCode(): Long {
        var appVersionCode: Long = 0
        try {
            val packageInfo = appContext.applicationContext
                .packageManager
                .getPackageInfo(appContext.packageName, 0)
            appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersionCode
    }

    fun getAppVersionName(): String {
        var appVersionName = ""
        try {
            val packageInfo = appContext.applicationContext
                .packageManager
                .getPackageInfo(appContext.packageName, 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appVersionName
    }

    fun getAppName(): String? {
        return try {
            val packageManager = appContext.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(
                appContext.packageName, 0
            )
            val labelRes: Int = packageInfo.applicationInfo.labelRes
            appContext.getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getManifestMetaValue(name: String): String {
        var metaValue = ""
        try {
            val packageManager = appContext.packageManager
            if (packageManager != null) {
                // 注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                val applicationInfo = packageManager.getApplicationInfo(
                    appContext.packageName,
                    PackageManager.GET_META_DATA
                )
                if (applicationInfo.metaData != null) {
                    metaValue = applicationInfo.metaData[name].toString()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return metaValue
    }

    fun String.getSubString(s: String, e: String): List<String> {
        val regex = "$s(.*?)$e"
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(this)
        val list: MutableList<String> = ArrayList()
        while (m.find()) {
            list.add(m.group(1))
        }
        return list
    }

    val Float.dp: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )

    val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()

    val Float.sp: Float                 // [xxhdpi](360 -> 1080)
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        )

    val Int.sp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()

    fun setFullScreen(window: Window) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun setColorStatusBar(
        window: Window,
        statusBarColor: Int,
        darkTextColor: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val decorView = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = darkTextColor
            window.statusBarColor = statusBarColor
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = statusBarColor
        }
    }

    fun getStatusBarHeight(): Int {
        val resourceId: Int = appContext.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return appContext.resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    fun getScreenHeight(includeVirtualKey: Boolean): Int {
        val display =
            (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val outPoint = Point()
        // 可能有虚拟按键的情况
        if (includeVirtualKey) display.getRealSize(outPoint)
        else display.getSize(outPoint)
        return outPoint.y
    }

    fun getScreenWidth(includeVirtualKey: Boolean): Int {
        val display =
            (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val outPoint = Point()
        // 可能有虚拟按键的情况
        if (includeVirtualKey) display.getRealSize(outPoint)
        else display.getSize(outPoint)
        return outPoint.x
    }

    fun String.isYearMonth(): Boolean {
        return Pattern.compile("[1-9][0-9]{3}(0[1-9]|1[0-2])").matcher(this).matches()
    }
}