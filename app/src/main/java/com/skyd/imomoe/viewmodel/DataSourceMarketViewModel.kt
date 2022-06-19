package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.arialyy.aria.core.download.DownloadEntity
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.DataSource1Bean
import com.skyd.imomoe.bean.DataSourceRepositoryBean
import com.skyd.imomoe.ext.dataSourceDirectoryChanged
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DataSourceService
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow


class DataSourceMarketViewModel : ViewModel() {
    var dataSourceMarketList: MutableStateFlow<DataState<List<Any>>> =
        MutableStateFlow(DataState.Empty)
    var localDataSourceMap = hashMapOf<String, DataSource1Bean>()
    val askAddUrlMap: MutableSharedFlow<Boolean> = MutableSharedFlow(extraBufferCapacity = 1)

    init {
        getDataSourceMarketList()
    }

    fun getDataSourceMarketList(
        interfaceVersion: String = com.skyd.imomoe.model.interfaces.interfaceVersion
    ) {
        dataSourceMarketList.tryEmit(DataState.Refreshing)
        request(request = {
            RetrofitManager.get().create(DataSourceService::class.java).getDataSourceJson()
        }, success = {
            localDataSourceMap.clear()
            DataSourceManager.getDataSourceList(DataSourceManager.getJarDirectory())
                .forEach { item ->
                    localDataSourceMap[item.name] = item
                }
            it.dataSourceList.forEach { item ->
                item.interfaceVersion = interfaceVersion
                val local = localDataSourceMap[item.name]
                if (local == null) {
                    item.status = DataSourceRepositoryBean.Status.NONE
                } else if ((local.versionCode ?: -1) < item.versionCode) {
                    item.status = DataSourceRepositoryBean.Status.OUTDATED
                } else if ((local.versionCode ?: -1) == item.versionCode) {
                    item.status = DataSourceRepositoryBean.Status.NEWEST
                }
            }
            dataSourceMarketList.tryEmit(DataState.Success(it.dataSourceList))
        }, error = {
            if (it.message?.contains("timeout") == true) {
                askAddUrlMap.tryEmit(true)
            }
            dataSourceMarketList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun onTaskPreStart(
        entity: DownloadEntity,
        dataSourceTitleMap: HashMap<String, String>
    ) {
        request(request = {
            updateStatus(
                dataSourceTitleMap[entity.url],
                DataSourceRepositoryBean.Status.DOWNLOADING
            )
        }, success = {
            dataSourceMarketList.tryEmit(DataState.Success(it))
        }, error = {
            it.message?.showToast()
        })
    }

    fun onTaskRunning(entity: DownloadEntity) {
    }

    fun onTaskComplete(
        entity: DownloadEntity,
        dataSourceTitleMap: HashMap<String, String>
    ) {
        request(request = {
            val dataSourceTitle = dataSourceTitleMap[entity.url]
            updateStatus(
                dataSourceTitleMap[entity.url],
                DataSourceRepositoryBean.Status.NEWEST
            ).apply {
                appContext.getString(R.string.data_source_market_download_complete, dataSourceTitle)
                    .showToast()
                dataSourceDirectoryChanged.tryEmit(true)
            }
        }, success = {
            dataSourceMarketList.tryEmit(DataState.Success(it))
            if (DataSourceManager.customDataSourceInfo?.get("name") == dataSourceTitleMap[entity.url] ||
                DataSourceManager.dataSourceFileName.substringBeforeLast(".") == dataSourceTitleMap[entity.url]
            ) {
                DataSourceManager.clearCache()
                RetrofitManager.setInstanceNull()
                Util.restartApp()
            }
        }, error = {
            it.message?.showToast()
        })
    }

    fun onTaskCancel(
        entity: DownloadEntity,
        dataSourceTitleMap: HashMap<String, String>
    ) {
        request(request = {
            var dataList = dataSourceMarketList.value.readOrNull().orEmpty()
            dataList = dataList.toMutableList().map {
                var result: Any = it
                if (it is DataSourceRepositoryBean && it.name == dataSourceTitleMap[entity.url]) {
                    val local = localDataSourceMap[it.name]
                    val status: DataSourceRepositoryBean.Status = if (local == null) {
                        DataSourceRepositoryBean.Status.NONE
                    } else if ((local.versionCode ?: -1) < it.versionCode) {
                        DataSourceRepositoryBean.Status.OUTDATED
                    } else if ((local.versionCode ?: -1) == it.versionCode) {
                        DataSourceRepositoryBean.Status.NEWEST
                    } else DataSourceRepositoryBean.Status.NONE
                    // 若最新数据有变化，则new一个新的bean替换之前的bean
                    // 注意：此处必须要new，不能直接更改之前的bean，否则Diff检测不出差异（旧数据被更改）
                    result = (it.clone() as DataSourceRepositoryBean).apply {
                        this.status = status
                    }
                }
                result
            }
            dataList
        }, success = {
            dataSourceMarketList.tryEmit(DataState.Success(it))
        }, error = {
            it.message?.showToast()
        })
    }

    private fun updateStatus(
        dataSourceTitle: String?,
        status: DataSourceRepositoryBean.Status
    ): List<Any> {
        var dataList = dataSourceMarketList.value.readOrNull().orEmpty()
        dataList = dataList.toMutableList().map {
            var result: Any = it
            if (it is DataSourceRepositoryBean && it.name == dataSourceTitle) {
                // 若最新数据有变化，则new一个新的bean替换之前的bean
                // 注意：此处必须要new，不能直接更改之前的bean，否则Diff检测不出差异（旧数据被更改）
                result = (it.clone() as DataSourceRepositoryBean).apply {
                    this.status = status
                }
            }
            result
        }
        return dataList
    }
}