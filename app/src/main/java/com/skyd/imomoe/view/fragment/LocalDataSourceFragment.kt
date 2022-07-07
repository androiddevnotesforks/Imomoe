package com.skyd.imomoe.view.fragment

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource1Bean
import com.skyd.imomoe.databinding.FragmentLocalDataSourceBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.activity.ConfigDataSourceActivity
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.DataSource1Proxy
import com.skyd.imomoe.viewmodel.LocalDataSourceViewModel

class LocalDataSourceFragment : BaseFragment<FragmentLocalDataSourceBinding>() {
    private val viewModel: LocalDataSourceViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(DataSource1Proxy(
            onClickListener = { _, data, _ ->
                val configDataSourceActivity = activity ?: return@DataSource1Proxy
                if (data.selected) {
                    configDataSourceActivity.showSnackbar(getString(R.string.the_data_source_is_using_now))
                } else {
                    if (configDataSourceActivity is ConfigDataSourceActivity) {
                        configDataSourceActivity.setDataSource(data.file.name)
                    } else {
                        configDataSourceActivity.showSnackbar(getString(R.string.activity_is_not_config_data_source_activity))
                    }
                }
            },
            onLongClickListener = { holder, data, _ ->
                showItemMenu(holder.itemView, data)
                true
            }
        )))
    }

    fun getDataSourceList() = viewModel.getDataSourceList()

    override fun onResume() {
        super.onResume()
        if (isFirstLoadData) {
            initData()
            isFirstLoadData = false
        }
    }

    private fun initData() {
        mBinding.apply {
            root.addFitsSystemWindows(right = true, bottom = true)
            rvLocalDataSourceFragment.layoutManager = LinearLayoutManager(activity)
            rvLocalDataSourceFragment.adapter = adapter
        }

        viewModel.dataSourceList.collectWithLifecycle(viewLifecycleOwner) { data ->
            when (data) {
                is DataState.Success -> {
                    adapter.dataList = data.data
                }
                else -> {}
            }
        }

        dataSourceDirectoryChanged.collectWithLifecycle(viewLifecycleOwner) { data ->
            viewModel.getDataSourceList()
        }
    }

    private fun showItemMenu(v: View, data: DataSource1Bean) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.menu_local_data_source_fragment_item, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_local_data_source_fragment_delete_item -> {
                    val activity = this.activity ?: return@setOnMenuItemClickListener true
                    if (activity is ConfigDataSourceActivity) {
                        activity.deleteDataSource(data)
                    } else {
                        activity.showSnackbar(getString(R.string.activity_is_not_config_data_source_activity))
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLocalDataSourceBinding.inflate(layoutInflater)
}