package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityRankBinding
import com.skyd.imomoe.ext.hideToolbarWhenCollapsed
import com.skyd.imomoe.view.fragment.RankFragment
import com.skyd.imomoe.view.listener.dsl.addOnTabSelectedListener
import com.skyd.imomoe.viewmodel.RankViewModel

class RankActivity : BaseActivity<ActivityRankBinding>() {
    private val viewModel: RankViewModel by viewModels()
    private val adapter: VpAdapter by lazy { VpAdapter() }
    private var offscreenPageLimit = 1
    private var selectedTabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
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
                if (position < viewModel.mldRankData.value?.size ?: 0)
                    tab.text = viewModel.mldRankData.value?.get(position)?.title
            }
            tabLayoutMediator.attach()

            ablRankActivity.hideToolbarWhenCollapsed(tbRankActivity)
        }


        viewModel.mldRankData.observe(this) {
            if (it != null) {
                hideLoadFailedTip()
                if (it.isNotEmpty()) mBinding.vp2RankActivity.offscreenPageLimit = it.size
            } else {
                showLoadFailedTip(getString(R.string.load_data_failed_click_to_retry)) {
                    viewModel.getRankTabData()
                    hideLoadFailedTip()
                }
            }
            adapter.notifyDataSetChanged()
            viewModel.isRequesting = false
        }

        if (viewModel.mldRankData.value == null) viewModel.getRankTabData()
    }

    override fun getBinding() = ActivityRankBinding.inflate(layoutInflater)

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_left_out)
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutRankActivityLoadFailed

    inner class VpAdapter : FragmentStateAdapter(this) {

        override fun getItemCount() = viewModel.mldRankData.value?.size ?: 0

        override fun createFragment(position: Int): Fragment {
            val fragment = RankFragment()
            val bundle = Bundle()
            bundle.putString("partUrl", viewModel.mldRankData.value?.get(position)?.actionUrl)
            fragment.arguments = bundle
            return fragment
        }
    }
}
