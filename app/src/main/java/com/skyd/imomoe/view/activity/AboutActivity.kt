package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.databinding.ActivityAboutBinding
import com.skyd.imomoe.ext.hideToolbarWhenCollapsed
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.ext.toHtml
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.openBrowser
import java.net.URL
import java.util.*

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            tbAboutActivity.setNavigationOnClickListener { finish() }

            tbAboutActivity.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_about_activity_info -> {
                        showMessageDialog(
                            title = getString(R.string.attention),
                            message = "本软件免费开源，严禁商用，支持Android 5.0+！仅在GitHub仓库长期发布！\n不介意的话可以给我的GitHub仓库点个Star",
                            positiveText = "去点Star",
                            negativeText = getString(R.string.cancel),
                            onPositive = { _, _ -> openBrowser(Const.Common.GITHUB_URL) },
                            onNegative = { dialog, _ -> dialog.dismiss() }
                        )
                        true
                    }
                    else -> false
                }
            }

            val c: Calendar = Calendar.getInstance()
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            if (month == Calendar.DECEMBER && (day > 21 || day < 29)) {     // 圣诞节彩蛋
                ivAboutActivityIconEgg.visible()
                ivAboutActivityIconEgg.setImageResource(R.drawable.ic_christmas_hat)
            }

            tvAboutActivityVersion.text =
                getString(R.string.app_version_name, Util.getAppVersionName()) + "\n" +
                        getString(R.string.app_version_code, Util.getAppVersionCode()) + "\n" +
                        getString(
                            R.string.data_source_interface_version,
                            com.skyd.imomoe.model.interfaces.interfaceVersion
                        )

            rlAboutActivityImomoe.setOnClickListener {
                var warningString: String = getString(R.string.jump_to_data_source_website_warning)
                if (URL(Api.MAIN_URL).protocol == "http") {
                    warningString =
                        getString(R.string.jump_to_browser_http_warning) + "\n" + warningString
                }
                showMessageDialog(
                    message = warningString,
                    positiveText = getString(R.string.still_to_visit),
                    onPositive = { _, _ -> openBrowser(Api.MAIN_URL) },
                    onNegative = { dialog, _ -> dialog.dismiss() }
                )
            }

            ivAboutActivityCustomDataSourceAbout.setOnClickListener {
                showMessageDialog(
                    title = getString(R.string.data_source_info),
                    message = (DataSourceManager.getConst()
                        ?: com.skyd.imomoe.model.impls.Const()).run {
                        "${
                            getString(
                                R.string.data_source_jar_version_name,
                                versionName()
                            )
                        }\n${
                            getString(
                                R.string.data_source_jar_version_code,
                                versionCode().toString()
                            )
                        }\n${about()}"
                    },
                    onPositive = { dialog, _ -> dialog.dismiss() }
                )
            }

            rlAboutActivityGithub.setOnClickListener {
                openBrowser(Const.Common.GITHUB_URL)
            }

            rlAboutActivityLicense.setOnClickListener {
                startActivity(Intent(this@AboutActivity, LicenseActivity::class.java))
            }

            rlAboutActivityUserNotice.setOnClickListener {
                showMessageDialog(
                    title = getString(R.string.user_notice),
                    message = Util.getUserNoticeContent().toHtml(),
                    cancelable = false,
                    onPositive = { _, _ ->
                        Util.setReadUserNoticeVersion(Const.Common.USER_NOTICE_VERSION)
                    }
                )
            }

            rlAboutActivityTestDevice.setOnClickListener {
                showMessageDialog(
                    title = getString(R.string.test_device),
                    message = "Physical Device: \nAndroid 10",
                    onPositive = { dialog, _ -> dialog.dismiss() }
                )
            }

            rlAboutActivityThanks.setOnClickListener {
                showMessageDialog(
                    title = getString(R.string.about_activity_thanks),
                    message = "MaybeQHL提供弹幕服务器：\nhttps://github.com/MaybeQHL/my_danmu_pub",
                    positiveText = getString(R.string.about_activity_open_danmaku_server_page),
                    onPositive = { _, _ -> openBrowser("https://github.com/MaybeQHL/my_danmu_pub") },
                    onNegative = { dialog, _ -> dialog.dismiss() }
                )
            }
            if (resources.getBoolean(R.bool.is_landscape)) {
                ablAboutActivity.hideToolbarWhenCollapsed(tbAboutActivity)
            }
        }
    }

    override fun getBinding(): ActivityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
}