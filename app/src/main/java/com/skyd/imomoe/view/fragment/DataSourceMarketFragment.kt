package com.skyd.imomoe.view.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arialyy.aria.core.task.DownloadTask
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource2Bean
import com.skyd.imomoe.databinding.FragmentDataSourceMarketBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.UrlMapActivityProcessor
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.market.DataSourceDownloadService
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.DataSource2Proxy
import com.skyd.imomoe.view.listener.dsl.requestPermissions
import com.skyd.imomoe.viewmodel.DataSourceMarketViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow

class DataSourceMarketFragment : BaseFragment<FragmentDataSourceMarketBinding>() {
    companion object {
        val needRefresh: MutableSharedFlow<Boolean> = MutableSharedFlow(extraBufferCapacity = 1)
    }

    private val viewModel: DataSourceMarketViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(DataSource2Proxy(
            onActionClickListener = { _, data, _ ->
                XXPermissions.with(this)
                    .permission(
                        Permission.MANAGE_EXTERNAL_STORAGE,
                        Permission.NOTIFICATION_SERVICE
                    )
                    .requestPermissions {
                        onGranted { permissions, all ->
                            if (!all) {
                                if (permissions?.contains(Permission.MANAGE_EXTERNAL_STORAGE) == false) {
                                    getString(R.string.no_storage_can_not_download).showToast()
                                }
                                if (permissions?.contains(Permission.NOTIFICATION_SERVICE) == false) {
                                    getString(R.string.no_notification_service).showToast()
                                }
                                return@onGranted
                            }
                            if (DataSourceManager.customDataSourceInfo?.get("name") == data.name ||
                                DataSourceManager.dataSourceFileName.substringBeforeLast(".") == data.name
                            ) {
                                showMessageDialog(
                                    icon = R.drawable.ic_warning_2_24,
                                    message = getString(R.string.data_source_market_restart_after_downloaded)
                                ) { _, _ ->
                                    startDownload(data)
                                }
                            } else {
                                startDownload(data)
                            }
                        }
                        onDenied { permissions, _ ->
                            if (permissions?.contains(Permission.MANAGE_EXTERNAL_STORAGE) == false) {
                                getString(R.string.no_storage_can_not_download).showToast()
                            }
                            if (permissions?.contains(Permission.NOTIFICATION_SERVICE) == false) {
                                getString(R.string.no_notification_service).showToast()
                            }
                        }
                    }
            }
        )))
    }

    private var binder: DataSourceDownloadService.DataSourceDownloadBinder? = null

    private fun startDownload(data: DataSource2Bean) {
        requireActivity().startService(
            Intent(activity, DataSourceDownloadService::class.java)
                .putExtra(
                    DataSourceDownloadService.DOWNLOAD_URL_KEY,
                    data.downloadUrl
                )
                .putExtra(
                    DataSourceDownloadService.DATA_SOURCE_TITLE,
                    data.name
                )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.root.addFitsSystemWindows(right = true, bottom = true)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstLoadData) {
            initData()
            isFirstLoadData = false
        }
    }

    private fun initData() {
        mBinding.apply {
            rvDataSourceMarketFragment.layoutManager = LinearLayoutManager(activity)
            rvDataSourceMarketFragment.adapter = adapter

            srlDataSourceMarketFragment.setOnRefreshListener { viewModel.getDataSourceMarketList() }
            srlDataSourceMarketFragment.setEnableLoadMore(false)
        }

        viewModel.dataSourceMarketList.collectWithLifecycle(viewLifecycleOwner) {
            when (it) {
                is DataState.Refreshing -> {
                    mBinding.srlDataSourceMarketFragment.autoRefreshAnimationOnly()
                }
                is DataState.Success -> {
                    adapter.dataList = it.data
                    mBinding.srlDataSourceMarketFragment.finishRefresh()
                }
                else -> {
                    adapter.dataList = emptyList()
                    mBinding.srlDataSourceMarketFragment.finishRefresh()
                }
            }
        }

        needRefresh.collectWithLifecycle(viewLifecycleOwner) {
            if (it) mBinding.srlDataSourceMarketFragment.autoRefresh()
        }

        viewModel.askAddUrlMap.collectWithLifecycle(viewLifecycleOwner) {
            if (it) {
                showMessageDialog(
                    title = getString(R.string.ask),
                    message = getString(R.string.data_source_market_ask_add_url_map),
                    onNegative = { dialog, _ -> dialog.dismiss() }
                ) { _, _ ->
                    UrlMapActivityProcessor.route.buildRouteUri {
                        appendQueryParameter(
                            UrlMapActivityProcessor.JSON_DATA,
                            requireActivity().getRawString(R.raw.github_url_map)
                        )
                        appendQueryParameter(
                            UrlMapActivityProcessor.AUTO_ADD_AND_FINISH, "true"
                        )
                        appendQueryParameter(
                            UrlMapActivityProcessor.ENABLED, "true"
                        )
                    }.route(requireActivity())
                }
            }
        }

        requireActivity().bindService(
            Intent(activity, DataSourceDownloadService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            binder = (service as? DataSourceDownloadService.DataSourceDownloadBinder)?.apply {
//                viewModel.initList(notCompleteList, dataSourceTitleMap)

                onTaskRunningEvent.collectWithLifecycle(this@DataSourceMarketFragment) { task ->
                    viewModel.onTaskRunning(task.downloadEntity)
                }
                onTaskCompleteEvent.collectWithLifecycle(this@DataSourceMarketFragment) { task ->
                    viewModel.onTaskComplete(task.downloadEntity, dataSourceTitleMap)
                }
                onTaskCancelEvent.collectWithLifecycle(this@DataSourceMarketFragment) { task ->
                    viewModel.onTaskCancel(task.downloadEntity, dataSourceTitleMap)
                }
                onTaskStopEvent.collectWithLifecycle(this@DataSourceMarketFragment) { task ->
                    viewModel.onTaskCancel(task.downloadEntity, dataSourceTitleMap)
                }
                onTaskFailEvent.collectWithLifecycle(this@DataSourceMarketFragment) { task ->
                    viewModel.onTaskCancel(task.downloadEntity, dataSourceTitleMap)
                }
                onTaskResumeEvent.collectWithLifecycle(this@DataSourceMarketFragment) {
                }
                onTaskPreEvent.collectWithLifecycle(this@DataSourceMarketFragment, onTaskPreStart)
                onTaskStartEvent.collectWithLifecycle(this@DataSourceMarketFragment, onTaskPreStart)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            binder = null
        }
    }

    private val onTaskPreStart: suspend CoroutineScope.(data: DownloadTask) -> Unit = { task ->
        val binder = this@DataSourceMarketFragment.binder
        if (binder != null) {
            viewModel.onTaskPreStart(task.downloadEntity, binder.dataSourceTitleMap)
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDataSourceMarketBinding.inflate(layoutInflater)
}