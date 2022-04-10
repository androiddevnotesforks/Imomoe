package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityAnimeDownloadBinding
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.requestManageExternalStorage
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover7Proxy
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel

class AnimeDownloadActivity : BaseActivity<ActivityAnimeDownloadBinding>() {
    private val viewModel: AnimeDownloadViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy { VarietyAdapter(mutableListOf(AnimeCover7Proxy())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mode = intent.getIntExtra("mode", 0)
        viewModel.actionBarTitle =
            intent.getStringExtra("actionBarTitle") ?: getString(R.string.download_anime)
        viewModel.directoryName = intent.getStringExtra("directoryName").orEmpty()
        viewModel.path = intent.getIntExtra("path", 0)

        mBinding.run {
            tbAnimeDownloadActivity.title = viewModel.actionBarTitle
            tbAnimeDownloadActivity.setNavigationOnClickListener { finish() }

            rvAnimeDownloadActivity.layoutManager = LinearLayoutManager(this@AnimeDownloadActivity)
            rvAnimeDownloadActivity.adapter = adapter

            layoutAnimeDownloadLoading.tvCircleProgressTextTip1.text =
                getString(R.string.read_download_data_file)
        }

        viewModel.mldAnimeCoverList.observe(this) {
            mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.gone()
            if (it != null) {
                if (it.isEmpty()) showLoadFailedTip(getString(R.string.no_download_video))
                adapter.dataList = it
            }
        }

        requestManageExternalStorage {
            onGranted {
                if (viewModel.mode == 0 && viewModel.mldAnimeCoverList.value == null) {
                    viewModel.getAnimeCover()
                } else if (viewModel.mode == 1) {
                    mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.visible()
                    if (viewModel.mldAnimeCoverList.value == null) {
                        viewModel.getAnimeCoverEpisode(viewModel.directoryName, viewModel.path)
                    }
                }
            }
            onDenied {
                getString(R.string.no_storage_permission_can_not_olay_local_video).showToast(Toast.LENGTH_LONG)
                finish()
            }
        }
    }

    override fun getBinding() = ActivityAnimeDownloadBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutAnimeDownloadNoDownload
}
