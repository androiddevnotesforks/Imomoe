package com.skyd.imomoe.view.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.databinding.ActivityDownloadManagerBinding
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadService
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeDownload1Proxy
import com.skyd.imomoe.viewmodel.DownloadManagerViewModel

class DownloadManagerActivity : BaseActivity<ActivityDownloadManagerBinding>() {
    private val viewModel: DownloadManagerViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                AnimeDownload1Proxy(
                    onCancelClickListener = { _, data, _ ->
                        AnimeDownloadService.mldCancelTask.postValue(data.id to data.url)
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

            rvDownloadManagerActivity.layoutManager =
                LinearLayoutManager(this@DownloadManagerActivity)
            rvDownloadManagerActivity.adapter = adapter
        }

        bindService(
            Intent(this, AnimeDownloadService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        viewModel.mldDataList.observe(this) {
            adapter.dataList = it.orEmpty()
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            binder = (service as? AnimeDownloadService.AnimeDownloadBinder)?.apply {
                viewModel.initList(notCompleteList, animeTitleEpisodeMap)

                mldOnTaskRunning.observe(this@DownloadManagerActivity) { task ->
                    task ?: return@observe
                    viewModel.onTaskRunning(task.downloadEntity)
                }
                mldOnTaskComplete.observe(this@DownloadManagerActivity) { task ->
                    task ?: return@observe
                    viewModel.onTaskComplete(task.downloadEntity)
                }
                mldOnTaskCancel.observe(this@DownloadManagerActivity) { task ->
                    task ?: return@observe
                    viewModel.onTaskCancel(task.downloadEntity)
                }
                mldOnTaskPre.observe(this@DownloadManagerActivity, onTaskPreStart)
                mldOnTaskStart.observe(this@DownloadManagerActivity, onTaskPreStart)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    private val onTaskPreStart: (DownloadTask?) -> Unit = { task ->
        val binder = this.binder
        if (task != null && binder != null) {
            viewModel.onTaskPreStart(task.downloadEntity, binder.animeTitleEpisodeMap)
        }
    }

    override fun getBinding() = ActivityDownloadManagerBinding.inflate(layoutInflater)
}