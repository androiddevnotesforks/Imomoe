package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.More1Bean
import com.skyd.imomoe.databinding.FragmentMoreBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.processor.ConfigDataSourceActivityProcessor
import com.skyd.imomoe.route.processor.JumpByUrlProcessor
import com.skyd.imomoe.route.processor.StartActivityProcessor
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.More1Proxy

class MoreFragment : BaseFragment<FragmentMoreBinding>() {
    private val list: MutableList<Any> = ArrayList()
    private val adapter: VarietyAdapter = VarietyAdapter(mutableListOf(More1Proxy()))

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMoreBinding =
        FragmentMoreBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", HistoryActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.watch_history),
            R.drawable.ic_history_24
        )
        list += More1Bean(
            JumpByUrlProcessor.route,
            getString(R.string.skip_by_website),
            R.drawable.ic_insert_link_24
        )
        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", SkinActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.skin_center),
            R.drawable.ic_skin_32
        )
        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", DownloadManagerActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.download_manager),
            R.drawable.ic_cloud_download_24
        )
        list += More1Bean(
            ConfigDataSourceActivityProcessor.route.buildRouteUri {
                appendQueryParameter("selectPageIndex", "1")
            }.toString(),
            getString(R.string.data_source_market),
            R.drawable.ic_plugin_24
        )
        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", SettingActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.setting),
            R.drawable.ic_settings_24
        )
        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", BackupRestoreActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.backup_and_restore),
            R.drawable.ic_cloud_done_24
        )
        list += More1Bean(
            StartActivityProcessor.route.buildRouteUri {
                appendQueryParameter("cls", AboutActivity::class.qualifiedName)
            }.toString(),
            getString(R.string.about),
            R.drawable.ic_info_24
        )

        mBinding.run {
            ablMoreFragment.addFitsSystemWindows(right = true, top = true)
            rvMoreFragment.addFitsSystemWindows(right = true)
            rvMoreFragment.layoutManager = GridLayoutManager(
                activity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvMoreFragment.addItemDecoration(AnimeShowItemDecoration())
            rvMoreFragment.adapter = adapter
            adapter.dataList = list
        }
    }
}
