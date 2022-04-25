package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover7Bean
import com.skyd.imomoe.databinding.ActivityAnimeDownloadBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover7Proxy
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel

class AnimeDownloadActivity : BaseActivity<ActivityAnimeDownloadBinding>() {
    private val viewModel: AnimeDownloadViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(AnimeCover7Proxy(
            onLongClickListener = { holder, data, _ ->
                showItemMenu(
                    holder.itemView,
                    R.menu.menu_anime_download_activity_item,
                    data
                )
                true
            }
        )))
    }

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

        viewModel.animeCoverList.collectWithLifecycle(this) { data ->
            mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.gone()
            when (data) {
                is DataState.Success -> {
                    if (data.data.isEmpty()) showLoadFailedTip(getString(R.string.no_download_video))
                    adapter.dataList = data.data
                }
                else -> {}
            }
        }

        viewModel.delete.collectWithLifecycle(this) {
            dismissWaitingDialog()
            initData(force = true)
            showSnackbar(
                if (it.first) getString(R.string.anime_download_activity_delete_success, it.second)
                else getString(R.string.anime_download_activity_delete_failed, it.second)
            )
        }

        initData()
    }

    private fun initData(force: Boolean = false) {
        requestManageExternalStorage {
            onGranted {
                if (viewModel.mode == 0) {
                    if (viewModel.animeCoverList.value is DataState.Empty || force) {
                        viewModel.getAnimeCover()
                    }
                } else if (viewModel.mode == 1) {
                    mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.visible()
                    if (viewModel.animeCoverList.value is DataState.Empty || force) {
                        viewModel.getAnimeCoverEpisode()
                    }
                }
            }
            onDenied {
                getString(R.string.no_storage_permission_can_not_olay_local_video).showToast(Toast.LENGTH_LONG)
                finish()
            }
        }
    }

    private fun showItemMenu(v: View, @MenuRes menuRes: Int, data: AnimeCover7Bean) {
        PopupMenu(this, v).apply {
            menuInflater.inflate(menuRes, menu)
            setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_item_anime_download_activity_item_delete -> {
                        showMessageDialog(
                            message = getString(
                                R.string.anime_download_activity_delete_message,
                                data.title
                            ),
                            icon = R.drawable.ic_delete_24,
                            onNegative = { dialog, _ -> dialog.dismiss() }
                        ) { _, _ ->
                            showWaitingDialog(
                                message = getString(R.string.anime_download_activity_deleting),
                                cancelable = false,
                                negativeText = getString(R.string.cancel),
                                onPositive = { dialog, _ ->
                                    viewModel.cancelDelete()
                                    dialog.dismiss()
                                }
                            )
                            viewModel.delete(data.path)
                        }
                    }
                }
                true
            }
            show()
        }
    }

    override fun getBinding() = ActivityAnimeDownloadBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutAnimeDownloadNoDownload
}
