package com.skyd.imomoe.model

import android.util.LruCache
import android.widget.Toast
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.editor2
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.ext.string
//import com.skyd.imomoe.model.impls.custom.TestClass
import com.skyd.imomoe.model.interfaces.IConst
import com.skyd.imomoe.model.interfaces.IRouteProcessor
import com.skyd.imomoe.model.interfaces.IUtil
import com.skyd.imomoe.model.interfaces.interfaceVersion
import com.skyd.imomoe.util.debug
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.showToast
import dalvik.system.DexClassLoader
import java.io.File
import java.util.jar.JarFile


object DataSourceManager {

    const val DEFAULT_DATA_SOURCE = ""

    var dataSourceName: String =
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
    private var customDataSourceInfo: HashMap<String, String>? = null
        get() {
            if (dataSourceName == DEFAULT_DATA_SOURCE) return null
            if (field == null) {
                val map: HashMap<String, String> = HashMap()
                runCatching {
                    val jar = JarFile(getJarPath())
                    jar.getInputStream(jar.getEntry("com/skyd/imomoe/model/impls/custom/CustomInfo"))
                        .string().split("\n").forEach {
                            it.split("=").let { kv ->
                                if (kv.size == 2) map[kv[0].trim()] = kv[1].trim()
                            }
                        }
                }.onFailure {
                    it.printStackTrace()
                }
                field = map
            }
            return field
        }

    fun getJarPath(): String =
        "${getJarDirectory()}/${dataSourceName}"

    fun getJarDirectory(): String {
        return "${appContext.getExternalFilesDir(null).toString()}/DataSourceJar"
    }

    fun <T> getBinaryName(clazz: Class<T>): String {
        return "com.skyd.imomoe.model.impls.custom.Custom${
            clazz.getDeclaredField("implName")
                .get(null)
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

    fun getRouterProcessor(): IRouteProcessor? {
        singletonCache[IRouteProcessor::class.java].let {
            if (it != null && it is IRouteProcessor) return it
        }
        return create(IRouteProcessor::class.java).apply {
            if (this != null) singletonCache.put(IRouteProcessor::class.java, this)
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
        if (dataSourceName == DEFAULT_DATA_SOURCE && !BuildConfig.DEBUG) return null
        if (interfaceVersion != customDataSourceInfo?.get("interfaceVersion") && !BuildConfig.DEBUG) {
            if (!showInterfaceVersionTip)
                appContext.getString(R.string.data_source_interface_version_not_match)
                    .showToast(Toast.LENGTH_LONG)
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
        } catch (e: Exception) {
            e.printStackTrace()
//            debug {
//                o = getTestClass(clazz)
//            }
        }
        if (clz != null) cache.put(clazz, clz)
        return o
    }

//    private fun <T> getTestClass(clazz: Class<T>): T? {
//        var o: T? = null
//        TestClass.classMap[clazz.simpleName].let {
//            if (it != null) o = it.newInstance() as T
//        }
//        return o
//    }
}