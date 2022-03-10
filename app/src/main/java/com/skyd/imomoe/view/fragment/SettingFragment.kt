package com.skyd.imomoe.view.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.net.DnsServer.selectDnsServer
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.update.AppUpdateHelper
import com.skyd.imomoe.util.update.AppUpdateStatus
import com.skyd.imomoe.view.activity.ConfigDataSourceActivity
import com.skyd.imomoe.view.component.preference.BasePreferenceFragment
import com.skyd.imomoe.view.component.preference.CheckBoxPreference
import com.skyd.imomoe.view.component.preference.Preference
import com.skyd.imomoe.viewmodel.SettingViewModel
import com.skyd.skin.SkinManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingFragment : BasePreferenceFragment() {
    private val viewModel: SettingViewModel by viewModels()
    private var selfUpdateCheck = false
    private val appUpdateHelper = AppUpdateHelper.instance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 清理历史记录
        viewModel.mldDeleteAllHistory.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it) getString(R.string.delete_all_history_succeed).showToast()
            else getString(R.string.delete_all_history_failed).showToast()
            viewModel.mldDeleteAllHistory.postValue(null)
        })
        viewModel.mldAllHistoryCount.observe(viewLifecycleOwner) {
            findPreference<Preference>("delete_all_history")?.text1 =
                getString(R.string.all_history_count, it)
        }
        viewModel.getAllHistoryCount()

        // 清理缓存文件
        viewModel.mldCacheSize.observe(viewLifecycleOwner) {
            findPreference<Preference>("clear_cache")?.text1 = it
        }
        viewModel.mldClearAllCache.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)
                viewModel.getCacheSize()
                if (it) getString(R.string.clear_cache_succeed).showToast()
                else getString(R.string.clear_cache_failed).showToast()
            }
            viewModel.mldClearAllCache.postValue(null)
        })
        viewModel.getCacheSize()

        appUpdateHelper.getUpdateStatus().observe(viewLifecycleOwner, Observer {
            val text1: String = when (it) {
                AppUpdateStatus.UNCHECK -> {
                    getString(R.string.uncheck_update)
//                    appUpdateHelper.checkUpdate()
                }
                AppUpdateStatus.CHECKING -> {
                    getString(R.string.checking_update)
                }
                AppUpdateStatus.DATED -> {
                    if (selfUpdateCheck) appUpdateHelper.noticeUpdate(requireActivity())
                    getString(R.string.find_new_version)
                }
                AppUpdateStatus.VALID -> {
                    getString(R.string.is_latest_version).apply {
                        if (selfUpdateCheck) showToast()
                    }
                }
                AppUpdateStatus.LATER -> {
                    getString(R.string.delay_update)
                }
                AppUpdateStatus.ERROR -> {
                    getString(R.string.check_update_failed).apply {
                        if (selfUpdateCheck) showToast()
                    }
                }
                else -> ""
            }
            findPreference<Preference>("update")?.text1 = text1
        })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("download_path")?.apply {
            summary = Const.DownloadAnime.animeFilePath
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    title(res = R.string.attention)
                    message(
                        text = "由于新版Android存储机制变更，因此新缓存的动漫将存储在App的私有路径，" +
                                "以前缓存的动漫依旧能够观看，其后面将有“旧”字样。新缓存的动漫与以前缓存的互不影响。" +
                                "\n\n注意：新缓存的动漫将在App被卸载或数据被清除后丢失。"
                    )
                    positiveButton { dismiss() }
                }
                false
            }
        }

        findPreference<Preference>("delete_all_history")?.apply {
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    icon(drawable = Util.getResDrawable(R.drawable.ic_delete_main_color_2_24_skin))
                    title(res = R.string.warning)
                    message(res = R.string.confirm_delete_all_history)
                    positiveButton(res = R.string.delete) { viewModel.deleteAllHistory() }
                    negativeButton(res = R.string.cancel) { dismiss() }
                }
                false
            }
        }

        findPreference<Preference>("clear_cache")?.apply {
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    icon(drawable = Util.getResDrawable(R.drawable.ic_sd_storage_main_color_2_24_skin))
                    title(res = R.string.warning)
                    message(text = "确定清理所有缓存？不包括缓存视频")
                    positiveButton(res = R.string.clean) { viewModel.clearAllCache() }
                    negativeButton(res = R.string.cancel) { dismiss() }
                }
                false
            }
        }

        findPreference<Preference>("update")?.apply {
            summary = getString(R.string.current_version, Util.getAppVersionName())
            setOnPreferenceClickListener {
                selfUpdateCheck = true
                when (appUpdateHelper.getUpdateStatus().value) {
                    AppUpdateStatus.CHECKING -> {
                        "已在检查，请稍等...".showToast()
                    }
                    else -> appUpdateHelper.checkUpdate()
                }
                false
            }
        }

        findPreference<Preference>("custom_data_source")?.apply {
            setOnPreferenceClickListener {
                startActivity(Intent(activity, ConfigDataSourceActivity::class.java))
                false
            }
            title = getString(R.string.custom_data_source, DataSourceManager.dataSourceName.let {
                if (it == DataSourceManager.DEFAULT_DATA_SOURCE)
                    getString(R.string.default_data_source)
                else it
            })
        }

        findPreference<Preference>("dns_server")?.apply {
            setOnPreferenceClickListener {
                activity?.selectDnsServer()
                false
            }
        }

        findPreference<CheckBoxPreference>("dark_mode_follow_system")?.apply {
            isChecked = SkinManager.getDarkMode() == SkinManager.DARK_FOLLOW_SYSTEM
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    SkinManager.setDarkMode(SkinManager.DARK_FOLLOW_SYSTEM)
                } else {
                    SkinManager.setDarkMode(SkinManager.DARK_MODE_NO)
                }
                true
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                isVisible = false
            }
        }
    }
}