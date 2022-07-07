package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityRankBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.hideToolbarWhenCollapsed
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.fragment.RankFragment
import com.skyd.imomoe.view.listener.dsl.addOnTabSelectedListener
import com.skyd.imomoe.viewmodel.RankViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankActivity : BaseActivity<ActivityRankBinding>() {
    private val viewModel: RankViewModel by viewModels()
    private val adapter: VpAdapter by lazy { VpAdapter() }
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            ablRankActivity.hideToolbarWhenCollapsed(tbRankActivity)
            ablRankActivity.addFitsSystemWindows(right = true, top = true)

            tbRankActivity.setNavigationOnClickListener { finish() }

            vp2RankActivity.offscreenPageLimit = offscreenPageLimit

            tlRankActivity.addOnTabSelectedListener {
                onTabSelected { tab -> selectedTabIndex = tab?.position ?: return@onTabSelected }
            }

            //添加rv
            vp2RankActivity.adapter = adapter
            val tabLayoutMediator = TabLayoutMediator(
                tlRankActivity, vp2RankActivity.getViewPager()
            ) { tab, position ->
                val list = viewModel.rankData.value.readOrNull().orEmpty()
                if (position < list.size) {
                    tab.text = list[position].title
                }
            }
            tabLayoutMediator.attach()
        }


        viewModel.rankData.collectWithLifecycle(this) { data ->
            when (data) {
                is DataState.Success -> {
                    hideLoadFailedTip()
                    val list = data.data
                    if (list.isNotEmpty()) {
                        mBinding.vp2RankActivity.offscreenPageLimit = list.size
                    }
                    adapter.notifyDataSetChanged()
                }
                is DataState.Error -> {
                    showLoadFailedTip {
                        viewModel.getRankTabData()
                    }
                    adapter.notifyDataSetChanged()
                }
                else -> {}
            }
        }
    }

    override fun getBinding() = ActivityRankBinding.inflate(layoutInflater)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_left_out)
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutRankActivityLoadFailed

    inner class VpAdapter : FragmentStateAdapter(this) {

        override fun getItemCount() = viewModel.rankData.value.readOrNull().orEmpty().size

        override fun createFragment(position: Int): Fragment {
            val fragment = RankFragment()
            val bundle = Bundle()
            bundle.putString(
                "partUrl",
                viewModel.rankData.value.readOrNull().orEmpty()[position].route
            )
            fragment.arguments = bundle
            return fragment
        }
    }
}
