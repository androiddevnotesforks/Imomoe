package com.skyd.imomoe.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.databinding.ActivityConfigDataSourceBinding
import com.skyd.imomoe.ext.copyTo
import com.skyd.imomoe.ext.requestManageExternalStorage
import com.skyd.imomoe.ext.showSnackbar
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.DataSource1Proxy
import com.skyd.imomoe.viewmodel.ConfigDataSourceViewModel
import java.io.File


class ConfigDataSourceActivity : BaseActivity<ActivityConfigDataSourceBinding>() {
    private val viewModel: ConfigDataSourceViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(DataSource1Proxy(
            onClickListener = { _, data, _ ->
                if (data.selected) {
                    getString(R.string.the_data_source_is_using_now).showSnackbar(this)
                } else setDataSource(data.file.name)
            },
            onLongClickListener = { _, data, _ ->
                deleteDataSource(data)
                true
            }
        )))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callToImport(intent)
        mBinding.apply {
            rvDataSourceConfigActivity.layoutManager =
                LinearLayoutManager(this@ConfigDataSourceActivity)
            rvDataSourceConfigActivity.adapter = adapter
            atbDataSourceConfigActivity.setBackButtonClickListener { finish() }
            atbDataSourceConfigActivity.setButtonClickListener(0) { resetDataSource() }
        }

        viewModel.mldDataSourceList.observe(this) { adapter.dataList = (it ?: emptyList()) }

        viewModel.getDataSourceList()
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
                            getString(
                                R.string.import_data_source_success,
                                uri.path
                            ).showSnackbar(this@ConfigDataSourceActivity)
                            viewModel.getDataSourceList()
                        },
                        onFailed = {
                            val msg =
                                "建议更换其他文件管理器后重试。" + (if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
                                    "Android 6及以下，请勿使用MT管理器打开ads文件，失败原因未知！若有解决方案，欢迎到Github仓库提PR"
                                else "") + "\n\n" + it.message
                            MaterialDialog(this@ConfigDataSourceActivity).show {
                                title(res = R.string.import_data_source_failed)
                                message(text = msg)
                                positiveButton(res = R.string.ok)
                            }
                        }
                    )
                }
                onDenied {
                    "无存储权限，无法导入".showSnackbar(this@ConfigDataSourceActivity, Toast.LENGTH_LONG)
                }
            }
        }
    }

    private fun resetDataSource(runBeforeReset: (() -> Unit)? = null) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.request_restart_app)
            positiveButton(res = R.string.restart) {
                runBeforeReset?.invoke()
                viewModel.resetDataSource()
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    private fun setDataSource(name: String, showDialog: Boolean = true) {
        if (!showDialog) {
            viewModel.setDataSource(name)
            return
        }
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.custom_data_source_tip)
            cancelable(false)
            positiveButton(res = R.string.restart) {
                viewModel.setDataSource(name)
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    private fun deleteDataSource(bean: DataSourceFileBean) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_category_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.ask_delete_data_source)
            positiveButton(res = R.string.ok) {
                if (DataSourceManager.dataSourceName == bean.file.name) {
                    resetDataSource { viewModel.deleteDataSource(bean) }
                } else {
                    viewModel.deleteDataSource(bean)
                }
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    private fun askOverwriteFile(needRestartApp: Boolean = false, callback: (Boolean) -> Unit) {
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.ask_overwrite_file)
            cancelable(false)
            positiveButton(
                res = if (needRestartApp) R.string.overwrite_file_and_restart
                else R.string.overwrite_file
            ) { callback.invoke(true) }
            negativeButton(res = R.string.do_not_overwrite_file) { callback.invoke(false) }
        }
    }

    private fun importDataSource(
        uri: Uri,
        onSuccess: ((File) -> Unit)? = null,
        onFailed: ((Exception) -> Unit)? = null
    ) {
        val dataSourceSuffix = (uri.path ?: "").substringAfterLast(".", "")
        if (!dataSourceSuffix.equals("ads", true)) {
            getString(R.string.invalid_data_source_suffix, dataSourceSuffix)
                .showSnackbar(this, duration = Toast.LENGTH_LONG)
            return
        }
        MaterialDialog(this).show {
            icon(drawable = Util.getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
            title(res = R.string.warning)
            message(res = R.string.import_data_source)
            cancelable(false)
            positiveButton(res = R.string.ok) {
                try {
                    val sourceFileName = uri.path!!.substringAfterLast("/")
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
            }
            negativeButton(res = R.string.cancel) {
                dismiss()
            }
        }
    }

    override fun getBinding(): ActivityConfigDataSourceBinding =
        ActivityConfigDataSourceBinding.inflate(layoutInflater)
}