package com.skyd.imomoe.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationBarView
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivityMainBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.Util.getUserNoticeContent
import com.skyd.imomoe.util.Util.lastReadUserNoticeVersion
import com.skyd.imomoe.util.Util.setReadUserNoticeVersion
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import com.skyd.imomoe.util.eventbus.MessageEvent
import com.skyd.imomoe.util.eventbus.SelectHomeTabEvent
import com.skyd.imomoe.util.html.source.WebSource.getWebSource
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.view.fragment.EverydayAnimeFragment
import com.skyd.imomoe.view.fragment.HomeFragment
import com.skyd.imomoe.view.fragment.MoreFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), EventBusSubscriber {
    @Inject
    lateinit var appUpdateHelper: AppUpdateHelper
    private var backPressTime = 0L
    private val adapter: VpAdapter by lazy { VpAdapter(this) }
    private lateinit var action: String

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (lastReadUserNoticeVersion() < Const.Common.USER_NOTICE_VERSION) {
            showMessageDialog(
                title = getString(R.string.user_notice_update),
                message = getUserNoticeContent().toHtml(),
                cancelable = false,
                positiveText = getString(R.string.agree),
                onPositive = { _, _ ->
                    setReadUserNoticeVersion(Const.Common.USER_NOTICE_VERSION)
                    initData()
                    initializeFlurry(application)
                },
                negativeText = getString(R.string.disagree_and_exit),
                onNegative = { _, _ -> finish() }
            )
        } else initData()
    }

    private fun initData() {
        doIntent(intent)
        action = intent.action.orEmpty()
        // 检查更新
        appUpdateHelper.getUpdateStatus().collectWithLifecycle(this) {
            when (it) {
                AppUpdateStatus.UNCHECK -> appUpdateHelper.checkUpdate()
                AppUpdateStatus.DATED -> appUpdateHelper.noticeUpdate(this@MainActivity)
                else -> Unit
            }
        }

        mBinding.vp2MainActivity.also {
            it.adapter = adapter
            it.fitsSystemWindows2()
            it.offscreenPageLimit = adapter.itemCount
            it.isUserInputEnabled = false
        }

        (mBinding.nvMainActivity as NavigationBarView).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_fragment -> {
                    mBinding.vp2MainActivity.setCurrentItem(0, false)
                    true
                }
                R.id.everyday_anime_fragment -> {
                    mBinding.vp2MainActivity.setCurrentItem(1, false)
                    true
                }
                R.id.more_fragment -> {
                    mBinding.vp2MainActivity.setCurrentItem(2, false)
                    true
                }
                else -> {
                    false
                }
            }
        }

        registerShortcuts()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        doIntent(intent)
    }

    // TODO
    private fun doIntent(intent: Intent?) {
        val uri: Uri = intent?.data ?: return
        runCatching {

        }.onFailure {
            logE(it.message.toString())
            it.message?.showToast()
        }
    }

    override fun getBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun processBackPressed() {
        val now = System.currentTimeMillis()
        if (now - backPressTime > 2000) {
            getString(R.string.press_again_to_exit).showToast()
            backPressTime = now
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            processBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onMessageEvent(event: MessageEvent) {
        when (event) {
            is SelectHomeTabEvent -> {
                mBinding.vp2MainActivity.setCurrentItem(0, false)
            }
        }
    }

    class VpAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount() = 3

        override fun createFragment(position: Int) = when (position) {
            0 -> HomeFragment()
            1 -> EverydayAnimeFragment()
            2 -> MoreFragment()
            else -> HomeFragment()
        }
    }
}
