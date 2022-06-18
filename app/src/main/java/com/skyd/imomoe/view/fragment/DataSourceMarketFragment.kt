package com.skyd.imomoe.view.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource2Bean
import com.skyd.imomoe.databinding.FragmentDataSourceMarketBinding
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.requestManageExternalStorage
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.market.DataSourceDownloadService
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.DataSource2Proxy
import com.skyd.imomoe.viewmodel.DataSourceMarketViewModel
import kotlinx.coroutines.CoroutineScope

class DataSourceMarketFragment : BaseFragment<FragmentDataSourceMarketBinding>() {
    private val viewModel: DataSourceMarketViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(DataSource2Proxy(
            onActionClickListener = { _, data, _ ->
                requestManageExternalStorage {
                    onGranted {
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
                    onDenied { getString(R.string.no_storage_can_not_download).showToast() }
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
                onTaskStopEvent.collectWithLifecycle(this@DataSourceMarketFragment) {
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