package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.More1Bean
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_LAUNCH_ACTIVITY
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_SKIP_BY_WEBSITE
import com.skyd.imomoe.databinding.FragmentMoreBinding
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.More1Proxy

class MoreFragment : BaseFragment<FragmentMoreBinding>() {
    private val list: MutableList<Any> = ArrayList()
    private val adapter: VarietyAdapter = VarietyAdapter(mutableListOf(More1Proxy()))

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMoreBinding =
        FragmentMoreBinding.inflate(inflater, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list += More1Bean(
            "$ANIME_LAUNCH_ACTIVITY/${HistoryActivity::class.qualifiedName}",
            getString(R.string.watch_history),
            R.drawable.ic_history_24
        )
        list += More1Bean(
            ANIME_SKIP_BY_WEBSITE,
            getString(R.string.skip_by_website),
            R.drawable.ic_insert_link_24
        )
        list += More1Bean(
            "$ANIME_LAUNCH_ACTIVITY/${SkinActivity::class.qualifiedName}",
            getString(R.string.skin_center),
            R.drawable.ic_skin_32
        )
        list += More1Bean(
            "$ANIME_LAUNCH_ACTIVITY/${SettingActivity::class.qualifiedName}",
            getString(R.string.setting),
            R.drawable.ic_settings_24
        )
        list += More1Bean(
            "$ANIME_LAUNCH_ACTIVITY/${BackupRestoreActivity::class.qualifiedName}",
            getString(R.string.backup_and_restore),
            R.drawable.ic_cloud_done_24
        )
        list += More1Bean(
            "$ANIME_LAUNCH_ACTIVITY/${AboutActivity::class.qualifiedName}",
            getString(R.string.about),
            R.drawable.ic_info_24
        )

        mBinding.run {
            rvMoreFragment.layoutManager = GridLayoutManager(activity, 2)
            rvMoreFragment.adapter = adapter
            adapter.dataList = list
        }
    }
}
