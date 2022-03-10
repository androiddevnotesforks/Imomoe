package com.skyd.imomoe.view.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.databinding.FragmentDataSourceMarketBinding
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.DataSource2Proxy
import com.skyd.imomoe.viewmodel.DataSourceMarketViewModel

class DataSourceMarketFragment : BaseFragment<FragmentDataSourceMarketBinding>() {
    private val viewModel: DataSourceMarketViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(DataSource2Proxy(
            onClickListener = { _, data, _ ->

            }
        )))
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
        }

        viewModel.mldDataSourceMarketList.observe(viewLifecycleOwner) {
            mBinding.srlDataSourceMarketFragment.finishRefresh()
            adapter.dataList = it.orEmpty()
        }

        mBinding.srlDataSourceMarketFragment.autoRefresh()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDataSourceMarketBinding.inflate(layoutInflater)

}