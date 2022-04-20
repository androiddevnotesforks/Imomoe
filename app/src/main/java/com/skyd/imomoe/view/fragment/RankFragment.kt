package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentRankBinding
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover11Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover3Proxy
import com.skyd.imomoe.viewmodel.RankListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankFragment : BaseFragment<FragmentRankBinding>() {
    private var partUrl: String = ""
    private val viewModel: RankListViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(AnimeCover3Proxy(), AnimeCover11Proxy()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val arguments = arguments
            partUrl = arguments?.getString("partUrl").orEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.showToast(Toast.LENGTH_LONG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.run {
            rvRankFragment.layoutManager = GridLayoutManager(activity, 4)
                .apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvRankFragment.addItemDecoration(AnimeShowItemDecoration())
            rvRankFragment.setHasFixedSize(true)
            rvRankFragment.adapter = adapter
            srlRankFragment.setOnRefreshListener { viewModel.getRankListData(partUrl) }
            srlRankFragment.setOnLoadMoreListener { viewModel.loadMoreRankListData() }
        }

        viewModel.mldRankData.observe(viewLifecycleOwner) {
            mBinding.srlRankFragment.closeHeaderOrFooter()
            if (it == null) {
                adapter.dataList = emptyList()
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getRankListData(partUrl)
                    hideLoadFailedTip()
                }
            } else {
                adapter.dataList = it
                hideLoadFailedTip()
            }
        }

        viewModel.mldLoadMoreRankData.observe(viewLifecycleOwner) {
            mBinding.srlRankFragment.closeHeaderOrFooter()
            if (it != null) {
                adapter.dataList += it
                hideLoadFailedTip()
            }
        }

        if (viewModel.mldRankData.value == null) mBinding.srlRankFragment.autoRefresh()
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRankBinding =
        FragmentRankBinding.inflate(inflater, container, false)
}