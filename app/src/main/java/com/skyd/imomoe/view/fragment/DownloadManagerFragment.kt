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
import android.widget.SeekBar
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.databinding.FragmentDownloadManagerBinding
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadService
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeDownload1Proxy


class DownloadManagerFragment : BaseFragment<FragmentDownloadManagerBinding>() {
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                AnimeDownload1Proxy(
                    onBindViewHolder = { holder, data, index ->

                    }
                )
            )
        )
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDownloadManagerBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().bindService(
            Intent(activity, AnimeDownloadService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder?) {
            (service as? AnimeDownloadService.AnimeDownloadBinder)?.apply {
                mldOnTaskRunning.observe(viewLifecycleOwner, onTaskRunning)
                mldOnTaskComplete.observe(viewLifecycleOwner, onTaskComplete)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    private val onTaskRunning: (DownloadTask) -> Unit = { it ->

    }

    private val onTaskComplete: (DownloadTask) -> Unit = { it ->

    }
}