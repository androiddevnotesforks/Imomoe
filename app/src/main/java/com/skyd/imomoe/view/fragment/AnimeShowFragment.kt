package com.skyd.imomoe.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentAnimeShowBinding
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.Banner1ViewHolder
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.*
import com.skyd.imomoe.viewmodel.AnimeShowViewModel


class AnimeShowFragment : BaseFragment<FragmentAnimeShowBinding>() {
    private var partUrl: String = ""
    private val viewModel: AnimeShowViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                AnimeCover1Proxy(),
                AnimeCover3Proxy(),
                AnimeCover4Proxy(),
                AnimeCover5Proxy(),
                Banner1Proxy(),
                Header1Proxy()
            )
        ).apply {
            onViewAttachedToWindow = {
                when (it) {
                    is Banner1ViewHolder -> it.banner1.startPlay(5000)
                }
            }

            onViewDetachedFromWindow = {
                when (it) {
                    is Banner1ViewHolder -> it.banner1.stopPlay()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = arguments

        runCatching {
            partUrl = arguments?.getString("partUrl").orEmpty()
            viewModel.viewPool =
                arguments?.getSerializable("viewPool") as SerializableRecycledViewPool
        }.onFailure {
            it.printStackTrace()
            it.message?.showToast(Toast.LENGTH_LONG)
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAnimeShowBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        if (isFirstLoadData) {
            initData()
            isFirstLoadData = false
        }
    }

    private fun initData() {
        mBinding.run {
            rvAnimeShowFragment.layoutManager = GridLayoutManager(activity, 4).apply {
                spanSizeLookup = AnimeShowSpanSize(adapter)
            }
            rvAnimeShowFragment.addItemDecoration(AnimeShowItemDecoration())
            rvAnimeShowFragment.adapter = adapter
            srlAnimeShowFragment.setOnRefreshListener {
                viewModel.getAnimeShowData(partUrl)
            }
            srlAnimeShowFragment.setOnLoadMoreListener {
                viewModel.loadMoreAnimeShowData()
            }
        }

        viewModel.viewPool?.let {
            mBinding.rvAnimeShowFragment.setRecycledViewPool(it)
        }

        viewModel.mldGetAnimeShowList.observe(viewLifecycleOwner) {
            mBinding.srlAnimeShowFragment.closeHeaderOrFooter()
            if (it == null) {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getAnimeShowData(partUrl)
                    hideLoadFailedTip()
                }
            } else {
                adapter.dataList = it
                hideLoadFailedTip()
            }
        }

        viewModel.mldLoadMoreAnimeShowList.observe(viewLifecycleOwner) {
            mBinding.srlAnimeShowFragment.closeHeaderOrFooter()
            if (it != null) {
                adapter.dataList += it
                hideLoadFailedTip()
            }
        }

        refresh()
    }

    fun refresh(): Boolean {
        return mBinding.srlAnimeShowFragment.autoRefresh()
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutAnimeShowFragmentLoadFailed

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        adapter.notifyDataSetChanged()
    }
}