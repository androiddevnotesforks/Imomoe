package com.skyd.imomoe.model

import android.util.LruCache
import android.widget.Toast
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.DataSource1Bean
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.model.interfaces.IConst
import com.skyd.imomoe.model.interfaces.IRouter
import com.skyd.imomoe.model.interfaces.IUtil
import com.skyd.imomoe.model.interfaces.interfaceVersion
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.showToast
import dalvik.system.DexClassLoader
import java.io.File
import java.util.jar.JarFile


object DataSourceManager {

    /**
     * 测试模式，仅供测试使用，设置为true并在APP内设置默认数据源后
     * 即会使用com.skyd.imomoe.model.impls.custom目录下的数据源
     */
    val testMode: Boolean = false

    const val DEFAULT_DATA_SOURCE = ""

    // 数据源文件名，例如：CustomDataSource1.ads
    var dataSourceFileName: String =
        sharedPreferences().getString("dataSourceName", DEFAULT_DATA_SOURCE)
            ?: DEFAULT_DATA_SOURCE
        get() {
            return if (field.isBlank() && sharedPreferences()
                    .getBoolean("customDataSource", false)
            ) {
                sharedPreferences().editor { putBoolean("customDataSource", false) }
                "CustomDataSource.jar"
            } else field
        }
        private set

    fun setDataSourceName(value: String) {
        sharedPreferences().editor { putString("dataSourceName", value) }
    }

    fun setDataSourceNameSynchronously(value: String) {
        sharedPreferences().editor2 { putString("dataSourceName", value) }
    }

    private var showInterfaceVersionTip: Boolean = false

    // 第一个是传入的接口，第二个是实现类
    private val cache: LruCache<Class<*>, Class<*>> = LruCache(10)
    private val singletonCache: LruCache<Class<*>, Any> = LruCache(5)
    var customDataSourceInfo: HashMap<String, String>? = null
        private set
        get() {
            if (dataSourceFileName == DEFAULT_DATA_SOURCE) return null
            if (field == null) {
                field = gerJarInfo(getJarPath())
            }
            return field
        }

    fun gerJarInfo(jarPath: String): HashMap<String, String> {
        val map: HashMap<String, String> = HashMap()
        runCatching {
            val jar = JarFile(jarPath)
            jar.getInputStream(jar.getEntry("CustomInfo"))
                .string().split("\n").forEach {
                    it.split("=").let { kv ->
                        if (kv.size == 2) map[kv[0].trim()] = kv[1].trim()
                    }
                }
        }.onFailure {
            it.printStackTrace()
        }
        return map
    }

    fun getJarPath(): String = "${getJarDirectory()}/${dataSourceFileName}"

    fun getJarDirectory(): String {
        return "${appContext.getExternalFilesDir(null).toString()}/DataSourceJar"
    }

    fun <T> getBinaryName(clazz: Class<T>): String {
        return "com.skyd.imomoe.model.impls.custom.Custom${
            clazz.getDeclaredField("implName").get(null)
        }"
    }

    fun getUtil(): IUtil? {
        singletonCache[IUtil::class.java].let {
            if (it != null && it is IUtil) return it
        }
        return create(IUtil::class.java).apply {
            if (this != null) singletonCache.put(IUtil::class.java, this)
        }
    }

    fun getRouter(): IRouter? {
        singletonCache[IRouter::class.java].let {
            if (it != null && it is IRouter) return it
        }
        return create(IRouter::class.java).apply {
            if (this != null) singletonCache.put(IRouter::class.java, this)
        }
    }

    fun getConst(): IConst? {
        singletonCache[IConst::class.java].let {
            if (it != null && it is IConst) return it
        }
        return create(IConst::class.java).apply {
            if (this != null) singletonCache.put(IConst::class.java, this)
        }
    }

    /**
     * 在更换数据源后必须调用此方法
     */
    fun clearCache() {
        cache.evictAll()
        singletonCache.evictAll()
        showInterfaceVersionTip = false
        customDataSourceInfo = null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(clazz: Class<T>): T? {
        // 如果不使用自定义数据，直接返回null
        if (dataSourceFileName == DEFAULT_DATA_SOURCE && !testMode) return null
        if (interfaceVersion != customDataSourceInfo?.get("interfaceVersion") && !testMode) {
            if (!showInterfaceVersionTip) appContext.getString(
                R.string.data_source_interface_version_not_match,
                customDataSourceInfo?.get("interfaceVersion"),
                interfaceVersion
            ).showToast(Toast.LENGTH_LONG)
            showInterfaceVersionTip = true
            return null
        }
        cache[clazz]?.let {
            return it.newInstance() as T
        }
        return innerCreate(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> innerCreate(clazz: Class<T>): T? {
        /**
         * 参数1 jarPath：待加载的jar文件路径，注意权限。jar必须是含dex的jar（dx --dex --output=dest.jar source.jar）
         * 参数2 optimizedDirectory：解压后的dex存放位置，此位置一定要是可读写且仅该应用可读写
         * 参数3 libraryPath：指向包含本地库(so)的文件夹路径，可以设为null
         * 参数4 parent：父级类加载器，一般可以通过Context.getClassLoader获取
         */
        val jarFile = File(getJarPath())
        if (!jarFile.exists() || !jarFile.isFile) {
            logE("DataSourceManager", "useCustomDataSource but jar doesn't exist")
            if (!BuildConfig.DEBUG) return null
        }
        val optimizedDirectory =
            File(appContext.getExternalFilesDir(null).toString() + "/DataSourceDex")
        if (!optimizedDirectory.exists() && !optimizedDirectory.mkdirs()) {
            logE("DataSourceManager", "can't create optimizedDirectory")
            return null
        }
        val classLoader =
            DexClassLoader(jarFile.path, optimizedDirectory.path, null, appContext.classLoader)
        var o: T? = null
        var clz: Class<*>? = null
        try {
            // 该方法将Class文件加载到内存时,并不会执行类的初始化,直到这个类第一次使用时才进行初始化.该方法因为需要得到一个ClassLoader对象
            clz = classLoader.loadClass(getBinaryName(clazz))
            o = clz.newInstance() as T
        } catch (e: Throwable) {
            e.printStackTrace()
            if (testMode) {
                o = getTestClass(clazz)
            }
        }
        if (clz != null) cache.put(clazz, clz)
        return o
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getTestClass(clazz: Class<T>): T? {
        var o: T? = null
        classMap[clazz.simpleName].let {
            if (it != null) {
                o = Class
                    .forName("com.skyd.imomoe.model.impls.$it")
                    .newInstance() as T
            }
        }
        return o
    }

    val classMap = hashMapOf(
        "IAnimeDetailModel" to "CustomAnimeDetailModel",
        "IAnimeShowModel" to "CustomAnimeShowModel",
        "IClassifyModel" to "CustomClassifyModel",
        "IEverydayAnimeModel" to "CustomEverydayAnimeModel",
        "IHomeModel" to "CustomHomeModel",
        "IMonthAnimeModel" to "CustomMonthAnimeModel",
        "IPlayModel" to "CustomPlayModel",
        "IRankModel" to "CustomRankModel",
        "ISearchModel" to "CustomSearchModel",
        "IConst" to "CustomConst",
        "IUtil" to "CustomUtil",
        "IRouter" to "CustomRouter",
        "IRankListModel" to "CustomRankListModel",
        "IEverydayAnimeWidgetModel" to "CustomEverydayAnimeWidgetModel"
    )

    fun getDataSourceList(directoryPath: String): List<DataSource1Bean> {
        val directory = File(directoryPath)
        return if (!directory.isDirectory) {
            emptyList()
        } else {
            val jarList = directory.listFiles { _, name ->
                name.endsWith(".ads", true) ||
                        name.endsWith(".jar", true)
            }
            jarList.orEmpty().map {
                val jarInfo = gerJarInfo(it.path)
                DataSource1Bean(
                    route = "", file = it, selected = it.name == dataSourceFileName,
                    name = jarInfo["name"] ?: it.name.substringBeforeLast("."),
                    versionCode = jarInfo["versionCode"]?.toIntOrNull(),
                    versionName = jarInfo["versionName"]
                )
            }.toList()
        }
    }
}