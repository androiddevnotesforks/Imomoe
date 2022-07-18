package com.skyd.imomoe.view.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.databinding.ActivityDownloadManagerBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadService
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeDownload1Proxy
import com.skyd.imomoe.viewmodel.DownloadManagerViewModel
import kotlinx.coroutines.CoroutineScope

class DownloadManagerActivity : BaseActivity<ActivityDownloadManagerBinding>() {
    private val viewModel: DownloadManagerViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                AnimeDownload1Proxy(
                    onCancelClickListener = { _, data, _ ->
                        AnimeDownloadService.cancelTaskEvent.tryEmit(data.id to data.url)
                    }, onPauseClickListener = { _, data, _ ->
                        AnimeDownloadService.stopTaskEvent.tryEmit(data.id)
                    }, onResumeClickListener = { _, data, _ ->
                        AnimeDownloadService.resumeTaskEvent.tryEmit(data.id)
                    }
                )
            )
        )
    }
    private var binder: AnimeDownloadService.AnimeDownloadBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            tbDownloadManagerActivity.setNavigationOnClickListener { finish() }
            ablDownloadManagerActivity.addFitsSystemWindows(right = true, top = true)

            rvDownloadManagerActivity.addFitsSystemWindows(right = true, bottom = true)
            rvDownloadManagerActivity.layoutManager = GridLayoutManager(
                this@DownloadManagerActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvDownloadManagerActivity.addItemDecoration(AnimeShowItemDecoration())
            rvDownloadManagerActivity.adapter = adapter
        }

        bindService(
            Intent(this, AnimeDownloadService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        viewModel.downloadDataList.collectWithLifecycle(this) {
            when (it) {
                is DataState.Success -> {
                    adapter.dataList = it.data
                }
                else -> {
                    adapter.dataList = emptyList()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            binder = (service as? AnimeDownloadService.AnimeDownloadBinder)?.apply {
                viewModel.initList(notCompleteList, animeTitleEpisodeMap)

                onTaskRunningEvent.collectWithLifecycle(this@DownloadManagerActivity) { task ->
                    viewModel.onTaskRunning(task.downloadEntity)
                }
                onTaskCompleteEvent.collectWithLifecycle(this@DownloadManagerActivity) { task ->
                    viewModel.onTaskComplete(task.downloadEntity)
                }
                onTaskCancelEvent.collectWithLifecycle(this@DownloadManagerActivity) { task ->
                    viewModel.onTaskCancel(task.downloadEntity)
                }
                onTaskStopEvent.collectWithLifecycle(this@DownloadManagerActivity) { task ->
                    viewModel.onTaskStateChanged(task.downloadEntity)
                }
                onTaskResumeEvent.collectWithLifecycle(this@DownloadManagerActivity) { task ->
                    viewModel.onTaskStateChanged(task.downloadEntity)
                }
                onTaskPreEvent.collectWithLifecycle(this@DownloadManagerActivity, onTaskPreStart)
                onTaskStartEvent.collectWithLifecycle(this@DownloadManagerActivity, onTaskPreStart)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            binder = null
        }
    }

    private val onTaskPreStart: suspend CoroutineScope.(data: DownloadTask) -> Unit = { task ->
        val binder = this@DownloadManagerActivity.binder
        if (binder != null) {
            viewModel.onTaskPreStart(task.downloadEntity, binder.animeTitleEpisodeMap)
        }
    }

    override fun getBinding() = ActivityDownloadManagerBinding.inflate(layoutInflater)
}