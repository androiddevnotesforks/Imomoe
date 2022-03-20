package com.skyd.imomoe.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.util.currentDate
import com.skyd.imomoe.util.killApplicationProcess
import com.skyd.imomoe.util.showToast
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.RuntimeException


class WebDavViewModel : ViewModel() {
    companion object {
        const val APP_DIR = "Imomoe"
        const val DATABASE_DIR = "${APP_DIR}/DataBase"
        const val TYPE_APP_DATABASE_DIR = "${DATABASE_DIR}/AppDataBase"
        val dataBaseName = mapOf(TYPE_APP_DATABASE_DIR to "app.db")
    }

    var pwd: String = ""
    var mldBackup: MutableLiveData<Pair<String, Boolean>> = MutableLiveData()
    var mldRestore: MutableLiveData<Pair<String, Boolean>> = MutableLiveData()
    var mldFileList: MutableLiveData<String> = MutableLiveData()
    val fileMap: HashMap<String, List<DavResource>?> = hashMapOf()

    private fun getAppDatabaseFile(): File {
        val version = getAppDataBase().openHelper.readableDatabase.version
        getAppDataBase().utilDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
        val dbName = dataBaseName[TYPE_APP_DATABASE_DIR]
        return App.context.getDatabasePath(dbName).copyTo(
            File(
                App.context.getExternalFilesDir(null).toString() +
                        "/Backup/AppDatabase/${dbName}-${currentDate("yyyyMMdd-HHmmss")}-" +
                        "${(0..99999).random()}.${version}"
            ), overwrite = true
        )
    }

    fun getFileList(server: String, username: String, password: String, type: String) {
        Thread {
            try {
                val sardine = OkHttpSardine()
                sardine.setCredentials(username, password)
                if (type == TYPE_APP_DATABASE_DIR) {
                    val version = getAppDataBase().openHelper.readableDatabase.version
                    val list = sardine.list("$server$TYPE_APP_DATABASE_DIR").filter {
                        !it.isDirectory && it.name.substringAfterLast(".").toInt() <= version
                    }
                    fileMap[type] = list
                    mldFileList.postValue(type)
                }
            } catch (e: Exception) {
                fileMap[type] = null
                mldFileList.postValue(type)
                e.printStackTrace()
                e.message?.showToast()
            }
        }.start()
    }

    fun backup(server: String, username: String, password: String, type: String) {
        Thread {
            try {
                val sardine = OkHttpSardine()
                sardine.setCredentials(username, password)
                if (type == TYPE_APP_DATABASE_DIR) {
                    val appDatabaseFile = getAppDatabaseFile()
                    if (!sardine.exists(server + APP_DIR)) {
                        sardine.createDirectory(server + APP_DIR)
                    }
                    if (!sardine.exists(server + DATABASE_DIR)) {
                        sardine.createDirectory(server + DATABASE_DIR)
                    }
                    if (!sardine.exists(server + TYPE_APP_DATABASE_DIR)) {
                        sardine.createDirectory(server + TYPE_APP_DATABASE_DIR)
                    }
                    sardine.put(
                        "$server$TYPE_APP_DATABASE_DIR/${appDatabaseFile.name}",
                        appDatabaseFile,
                        "*/*"
                    )
                    appDatabaseFile.delete()
                    mldBackup.postValue(type to true)
                }
            } catch (e: Exception) {
                mldBackup.postValue(type to false)
                e.printStackTrace()
                e.message?.showToast()
            }
        }.start()
    }

    fun restore(partUrl: String, credential: Triple<String, String, String>, type: String) {
        restore(partUrl, credential.first, credential.second, credential.third, type)
    }

    fun restore(partUrl: String, server: String, username: String, password: String, type: String) {
        Thread {
            try {
                val url = server.toUri().let { it.scheme + "://" + it.host + partUrl }
                val sardine = OkHttpSardine()
                sardine.setCredentials(username, password)
                if (type == TYPE_APP_DATABASE_DIR) {
                    val restoreDatabaseVersion = partUrl.substringAfterLast(".").toInt()
                    if (restoreDatabaseVersion > getAppDataBase().openHelper.readableDatabase.version) {
                        throw RuntimeException("the database version is newer, can't restore.")
                    }
                    val dbName = dataBaseName[TYPE_APP_DATABASE_DIR]
                    val shm = App.context.getDatabasePath("$dbName-shm")
                    val wal = App.context.getDatabasePath("$dbName-wal")
                    if (shm.exists()) if (!shm.delete()) throw RuntimeException("delete shm file failed")
                    if (wal.exists()) if (!wal.delete()) throw RuntimeException("delete wal file failed")
                    sardine.get(url).copyTo(App.context.getDatabasePath(dbName).outputStream())
                    App.context.getString(R.string.restore_app_database_succeed).showToast()
                    // 因为删除了shm和wal文件，因此APP进程需要结束/重启一下
                    App.context.killApplicationProcess()
                }
            } catch (e: Exception) {
                mldRestore.postValue(type to false)
                e.printStackTrace()
                e.message?.showToast()
            }
        }.start()
    }

    fun delete(data: DavResource, credential: Triple<String, String, String>, type: String) {
        delete(data, credential.first, credential.second, credential.third, type)
    }

    fun delete(
        data: DavResource, server: String, username: String, password: String, type: String
    ) {
        Thread {
            try {
                val url = server.toUri().let { it.scheme + "://" + it.host + data.path }
                val sardine = OkHttpSardine()
                sardine.setCredentials(username, password)
                sardine.delete(url)
                val list = fileMap[type]?.toMutableList()
                    ?: throw IllegalArgumentException("$type list not exists")
                if (list.remove(data)) fileMap[type] = list
                else throw IllegalArgumentException("$type doesn't contain data: $data")
                mldFileList.postValue(type)
                App.context.getString(R.string.delete_succeed).showToast()
            } catch (e: Exception) {
                e.printStackTrace()
                "${App.context.getString(R.string.delete_failed)}\n${e.message}".showToast()
            }
        }.start()
    }

}