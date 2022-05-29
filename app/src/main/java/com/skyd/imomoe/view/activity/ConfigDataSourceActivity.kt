package com.skyd.imomoe.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource1Bean
import com.skyd.imomoe.databinding.ActivityConfigDataSourceBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.fragment.LocalDataSourceFragment
import com.skyd.imomoe.viewmodel.ConfigDataSourceViewModel
import java.io.File


class ConfigDataSourceActivity : BaseActivity<ActivityConfigDataSourceBinding>() {
    private val viewModel: ConfigDataSourceViewModel by viewModels()
    private val adapter: VpAdapter by lazy { VpAdapter(this) }

    private val tabLayoutTitle by lazy {
        arrayOf(getString(R.string.local_data_source), getString(R.string.data_source_market))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.apply {
            tbConfigDataSourceActivity.apply {
                setNavigationOnClickListener { finish() }
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_item_config_data_source_activity_reset -> {
                            resetDataSource()
                            true
                        }
                        else -> false
                    }
                }
            }

            vp2ConfigDataSourceActivity.adapter = adapter

            val tabLayoutMediator = TabLayoutMediator(
                tlConfigDataSourceActivity, vp2ConfigDataSourceActivity.getViewPager()
            ) { tab, position ->
                tab.text = tabLayoutTitle[position % 2]
            }
            tabLayoutMediator.attach()
        }

        viewModel.deleteSource.collectWithLifecycle(this) {
            adapter.getFragment<LocalDataSourceFragment>(supportFragmentManager, 0)
                ?.getDataSourceList()
        }

        intent?.let { callToImport(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { callToImport(it) }
    }

    private fun callToImport(intent: Intent) {
        val uri = intent.data
        if (Intent.ACTION_VIEW == intent.action && uri != null) {
            requestManageExternalStorage {
                onGranted {
                    importDataSource(uri,
                        onSuccess = {
                            showSnackbar(getString(R.string.import_data_source_success, uri.path))
                            adapter.getFragment<LocalDataSourceFragment>(supportFragmentManager, 0)
                                ?.getDataSourceList()
                        },
                        onFailed = {
                            val msg =
                                "建议更换其他文件管理器后重试。" + (if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
                                    "Android 6及以下，请勿使用MT管理器打开ads文件，失败原因未知！若有解决方案，欢迎到GitHub仓库提PR"
                                else "") + "\n\n" + it.message
                            showMessageDialog(
                                title = getString(R.string.import_data_source_failed),
                                message = msg,
                                onPositive = { dialog, _ -> dialog.dismiss() }
                            )
                        }
                    )
                }
                onDenied {
                    showSnackbar("无存储权限，无法导入", Toast.LENGTH_LONG)
                }
            }
        }
    }

    private fun importDataSource(
        uri: Uri,
        onSuccess: ((File) -> Unit)? = null,
        onFailed: ((Exception) -> Unit)? = null
    ) {

        val dataSourceSuffix = uri.fileSuffixName(contentResolver)
        if (!dataSourceSuffix.equals("ads", true)) {
            showSnackbar(
                text = getString(R.string.invalid_data_source_suffix, dataSourceSuffix),
                duration = Toast.LENGTH_LONG
            )
            return
        }
        showMessageDialog(
            title = getString(R.string.warning),
            icon = R.drawable.ic_insert_drive_file_24,
            message = getString(R.string.import_data_source),
            cancelable = false,
            onPositive = { _, _ ->
                try {
                    val sourceFileName = uri.fileName(contentResolver)
                    val directory = File(DataSourceManager.getJarDirectory())
                    if (!directory.exists()) directory.mkdirs()
                    val target = File(
                        DataSourceManager.getJarDirectory() + "/" + sourceFileName
                    )
                    if (target.exists()) {
                        val needRestartApp = DataSourceManager.dataSourceName == sourceFileName
                        askOverwriteFile(needRestartApp) {
                            if (!it) onFailed?.invoke(
                                FileAlreadyExistsException(
                                    file = target,
                                    reason = "file already exists"
                                )
                            )
                            else {
                                Thread {
                                    try {
                                        uri.copyTo(target)
                                    } catch (e: Exception) {
                                        runOnUiThread { onFailed?.invoke(e) }
                                        return@Thread
                                    }
                                    if (needRestartApp) {
                                        viewModel.clearDataSourceCache()
                                        Util.restartApp()
                                    } else {
                                        runOnUiThread { onSuccess?.invoke(target) }
                                    }
                                }.start()
                            }
                        }
                    } else {
                        target.createNewFile()
                        Thread {
                            try {
                                uri.copyTo(target)
                            } catch (e: Exception) {
                                runOnUiThread { onFailed?.invoke(e) }
                                return@Thread
                            }
                            runOnUiThread { onSuccess?.invoke(target) }
                        }.start()
                    }
                } catch (e: Exception) {
                    onFailed?.invoke(e)
                }
            },
            onNegative = { dialog, _ -> dialog.dismiss() }
        )
    }

    private fun resetDataSource(runBeforeReset: (() -> Unit)? = null) {
        showMessageDialog(
            title = getString(R.string.warning),
            icon = R.drawable.ic_category_24,
            message = getString(R.string.request_restart_app),
            positiveText = getString(R.string.restart),
            onPositive = { _, _ ->
                runBeforeReset?.invoke()
                viewModel.resetDataSource()
            },
            onNegative = { dialog, _ -> dialog.dismiss() }
        )
    }

    fun setDataSource(name: String, showDialog: Boolean = true) {
        if (!showDialog) {
            viewModel.setDataSource(name)
            return
        }
        showMessageDialog(
            title = getString(R.string.warning),
            icon = R.drawable.ic_category_24,
            message = getString(R.string.custom_data_source_tip),
            cancelable = false,
            positiveText = getString(R.string.restart),
            onPositive = { _, _ -> viewModel.setDataSource(name) },
            onNegative = { dialog, _ -> dialog.dismiss() }
        )
    }

    fun deleteDataSource(bean: DataSource1Bean) {
        showMessageDialog(
            title = getString(R.string.warning),
            icon = R.drawable.ic_category_24,
            message = getString(R.string.ask_delete_data_source),
            onPositive = { _, _ ->
                if (DataSourceManager.dataSourceName == bean.file.name) {
                    resetDataSource { viewModel.deleteDataSource(bean) }
                } else {
                    viewModel.deleteDataSource(bean)
                }
            },
            onNegative = { dialog, _ -> dialog.dismiss() }
        )
    }

    private fun askOverwriteFile(needRestartApp: Boolean = false, callback: (Boolean) -> Unit) {
        showMessageDialog(
            title = getString(R.string.warning),
            icon = R.drawable.ic_insert_drive_file_24,
            message = getString(R.string.ask_overwrite_file),
            cancelable = false,
            positiveText = getString(
                if (needRestartApp) R.string.overwrite_file_and_restart
                else R.string.overwrite_file
            ),
            onPositive = { _, _ -> callback.invoke(true) },
            negativeText = getString(R.string.do_not_overwrite_file),
            onNegative = { _, _ -> callback.invoke(false) }
        )
    }

    override fun getBinding(): ActivityConfigDataSourceBinding =
        ActivityConfigDataSourceBinding.inflate(layoutInflater)

    class VpAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount() = 1

        override fun createFragment(position: Int) = when (position) {
            0 -> LocalDataSourceFragment()
            else -> LocalDataSourceFragment()
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Fragment> getFragment(fm: FragmentManager, id: Long): T? {
            return fm.findFragmentByTag("f$id") as? T
        }
    }
}