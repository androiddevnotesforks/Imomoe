package com.skyd.imomoe.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentLocalDataSourceBinding
import com.skyd.imomoe.ext.showSnackbar
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
            onLongClickListener = { _, data, _ ->
                val configDataSourceActivity = activity ?: return@DataSource1Proxy true
                if (configDataSourceActivity is ConfigDataSourceActivity) {
                    configDataSourceActivity.deleteDataSource(data)
                } else {
                    configDataSourceActivity.showSnackbar(getString(R.string.activity_is_not_config_data_source_activity))
                }
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
            rvLocalDataSourceFragment.layoutManager = LinearLayoutManager(activity)
            rvLocalDataSourceFragment.adapter = adapter
        }

        viewModel.mldDataSourceList.observe(viewLifecycleOwner) {
            adapter.dataList = (it ?: emptyList())
        }

        if (viewModel.mldDataSourceList.value == null) viewModel.getDataSourceList()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLocalDataSourceBinding.inflate(layoutInflater)
}