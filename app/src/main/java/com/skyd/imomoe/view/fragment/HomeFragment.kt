package com.skyd.imomoe.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentHomeBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.SearchActivityProcessor
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.activity.AnimeDownloadActivity
import com.skyd.imomoe.view.activity.ClassifyActivity
import com.skyd.imomoe.view.activity.FavoriteActivity
import com.skyd.imomoe.view.activity.RankActivity
import com.skyd.imomoe.view.listener.dsl.addOnTabSelectedListener
import com.skyd.imomoe.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private val adapter: VpAdapter by lazy { VpAdapter() }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.run {
            if (resources.getBoolean(R.bool.is_landscape)) {
                tbHomeFragment.addFitsSystemWindows(right = true)
                tlHomeFragment.addFitsSystemWindows(right = true)
            }

            vp2HomeFragment.adapter = adapter
            val tabLayoutMediator = TabLayoutMediator(
                tlHomeFragment, vp2HomeFragment.getViewPager()
            ) { tab, position ->
                val list = viewModel.allTabList.value.readOrNull().orEmpty()
                if (position < list.size) {
                    tab.text = list[position].title
                }
            }
            tabLayoutMediator.attach()

            tbHomeFragment.apply {
                setNavigationOnClickListener {
                    startActivity(Intent(requireActivity(), RankActivity::class.java))
                    requireActivity().overridePendingTransition(
                        R.anim.anl_push_left_in,
                        R.anim.anl_stay
                    )
                }

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_item_home_fragment_classify -> {
                            startActivity(Intent(activity, ClassifyActivity::class.java))
                            true
                        }
                        R.id.menu_item_home_fragment_download -> {
                            requestManageExternalStorage {
                                onGranted {
                                    startActivity(
                                        Intent(activity, AnimeDownloadActivity::class.java)
                                    )
                                }
                                onDenied { "无存储权限，无法播放本地缓存视频".showToast(Toast.LENGTH_LONG) }
                            }
                            true
                        }
                        R.id.menu_item_home_fragment_favorite -> {
                            startActivity(Intent(activity, FavoriteActivity::class.java))
                            true
                        }
                        else -> false
                    }

                }
            }

            btnHomeFragmentSearch.setOnClickListener {
                activity?.let {
                    SearchActivityProcessor.route.route(it)
                    it.overridePendingTransition(R.anim.anl_push_top_in, R.anim.anl_stay)
                }
            }

            tlHomeFragment.addOnTabSelectedListener {
                onTabSelected { viewModel.currentTab = it!!.position }
            }

            ablHomeFragment.hideToolbarWhenCollapsed(tbHomeFragment)
        }

        viewModel.allTabList.collectWithLifecycle(viewLifecycleOwner) { data ->
            when (data) {
                is DataState.Success -> {
                    hideLoadFailedTip()
                    if (data.data.isNotEmpty()) {
                        mBinding.vp2HomeFragment.offscreenPageLimit = data.data.size
                    }
                }
                is DataState.Error -> {
                    showLoadFailedTip {
                        viewModel.getAllTabData()
                    }
                }
                else -> {}
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutHomeFragmentLoadFailed

    inner class VpAdapter : FragmentStateAdapter(this) {

        override fun getItemCount() = viewModel.allTabList.value.readOrNull().orEmpty().size

        override fun createFragment(position: Int): Fragment {
            val fragment = AnimeShowFragment()
            val bundle = Bundle()
            bundle.putString(
                "partUrl",
                viewModel.allTabList.value.readOrNull().orEmpty()[position].route
            )
            fragment.arguments = bundle
            return fragment
        }
    }
}
