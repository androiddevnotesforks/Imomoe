package com.skyd.imomoe.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.secretSharedPreferences
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.ext.warningDialog
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.RestoreFile1Proxy
import com.skyd.imomoe.view.component.BottomSheetRecyclerView
import com.skyd.imomoe.view.component.preference.BasePreferenceFragment
import com.skyd.imomoe.view.component.preference.Preference
import com.skyd.imomoe.viewmodel.WebDavViewModel

class WebDavFragment : BasePreferenceFragment() {
    private val viewModel: WebDavViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mldBackup.observe(viewLifecycleOwner) {
            if (it.first == WebDavViewModel.TYPE_APP_DATABASE_DIR) {
                findPreference<Preference>("backup_app_database_to_cloud")?.text1 =
                    if (it.second) getString(R.string.backup_succeed)
                    else getString(R.string.backup_failed)
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.webdav_preferences, rootKey)

        findPreference<Preference>("webdav_server_address")?.apply {
            val address = App.context.sharedPreferences()
                .getString("webdav_server_address", null).orEmpty()
            summary = address
            setOnPreferenceClickListener {
                var url: String
                MaterialDialog(requireActivity()).input(
                    hintRes = R.string.webdav_server_address,
                    prefill = address
                ) { _, text ->
                    url = text.toString()
                    if (url.matches(Regex("^[a-zA-z]+://[^\\s]+"))) {
                        if (!url.endsWith("/")) url += "/"
                        App.context.sharedPreferences().editor {
                            putString("webdav_server_address", url)
                        }
                        summary = url
                    } else {
                        warningDialog(onPositive = { it.dismiss() })
                            .message(res = R.string.wrong_webdav_server_address_format).show()
                    }
                }.positiveButton(R.string.ok).show()
                false
            }
        }

        findPreference<Preference>("webdav_account")?.apply {
            val account = App.context.sharedPreferences()
                .getString("webdav_account", null).orEmpty()
            summary = account
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).input(
                    hintRes = R.string.webdav_account,
                    prefill = account
                ) { _, text ->
                    App.context.sharedPreferences().editor {
                        putString("webdav_account", text.toString())
                    }
                    summary = text
                }.positiveButton(R.string.ok).show()
                false
            }
        }

        findPreference<Preference>("webdav_password")?.apply {
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).input(hintRes = R.string.webdav_password) { _, text ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        secretSharedPreferences().editor {
                            putString("webdav_password", text.toString())
                        }
                    } else viewModel.pwd = text.toString()
                    summary = getString(R.string.webdav_password_set)
                }.positiveButton(R.string.ok).show()
                false
            }
            summary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (secretSharedPreferences().getString("webdav_password", null) != null) {
                    getString(R.string.webdav_password_set)
                } else getString(R.string.webdav_password_not_set)
            } else {
                if (viewModel.pwd.isNotEmpty()) getString(R.string.webdav_password_set)
                else getString(R.string.webdav_password_not_set)
            }
        }

        findPreference<Preference>("backup_app_database_to_cloud")?.apply {
            setOnPreferenceClickListener {
                val credential = getWebDAVCredential()
                if (!checkWebDAVCredential(
                        credential.first,
                        credential.second,
                        credential.third
                    )
                ) {
                    return@setOnPreferenceClickListener false
                }
                viewModel.backup(
                    credential.first, credential.second, credential.third,
                    WebDavViewModel.TYPE_APP_DATABASE_DIR
                )
                text1 = getString(R.string.backing_up)
                false
            }
        }

        findPreference<Preference>("restore_app_database_from_cloud")?.apply {
            setOnPreferenceClickListener {
                val credential = getWebDAVCredential()
                if (!checkWebDAVCredential(
                        credential.first,
                        credential.second,
                        credential.third
                    )
                ) {
                    return@setOnPreferenceClickListener false
                }
                viewModel.getFileList(
                    credential.first, credential.second, credential.third,
                    WebDavViewModel.TYPE_APP_DATABASE_DIR
                )
                showBottomSheetDialog(
                    WebDavViewModel.TYPE_APP_DATABASE_DIR,
                    R.string.restore_app_database_bottom_sheet_dialog_tip
                ).show()
                false
            }
        }
    }

    private fun getWebDAVCredential(): Triple<String, String, String> {
        val url = App.context.sharedPreferences()
            .getString("webdav_server_address", null).orEmpty()
        val account = App.context.sharedPreferences()
            .getString("webdav_account", null).orEmpty()
        val password = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            secretSharedPreferences().getString("webdav_password", null).orEmpty()
        } else {
            viewModel.pwd
        }
        return Triple(url, account, password)
    }

    private fun checkWebDAVCredential(
        url: String,
        account: String,
        password: String
    ): Boolean {
        if (url.isBlank()) {
            getString(R.string.webdav_server_not_set).showToast()
            return false
        }
        if (account.isBlank()) {
            getString(R.string.webdav_account_not_set).showToast()
            return false
        }
        if (password.isBlank()) {
            getString(R.string.webdav_password_not_set).showToast()
            return false
        }
        return true
    }

    private fun showBottomSheetDialog(
        type: String,
        @StringRes tipRes: Int = -1
    ): BottomSheetDialog {
        val bottomSheetDialog =
            BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(requireActivity(), R.layout.dialog_bottom_sheet_1, null)
        bottomSheetDialog.setContentView(contentView)
        val tips = contentView.findViewById<TextView>(R.id.tv_dialog_bottom_sheet_1_tips)
        if (tipRes != -1) tips.text = getString(tipRes)
        val recyclerView =
            contentView.findViewById<BottomSheetRecyclerView>(R.id.rv_dialog_bottom_sheet_1)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.post { recyclerView.scrollToPosition(0) }
        val adapter = VarietyAdapter(
            mutableListOf(RestoreFile1Proxy(onClickListener = { _, data, _ ->
                askRestore(type, warningDialog(onPositive = {
                    viewModel.restore(data.path, getWebDAVCredential(), type)
                    getString(R.string.restoring_data).showToast()
                    it.dismiss()
                }))
            }, onLongClickListener = { _, data, _ ->
                askDelete(type, warningDialog(onPositive = {
                    viewModel.delete(data, getWebDAVCredential(), type)
                }))
                true
            }))
        )
        val observer = Observer<String> {
            adapter.dataList = viewModel.fileMap[it] ?: emptyList()
        }
        viewModel.mldFileList.observe(this, observer)
        bottomSheetDialog.setOnDismissListener {
            viewModel.mldFileList.removeObserver(observer)
        }
        recyclerView.adapter = adapter
        logE(bottomSheetDialog.toString())
        return bottomSheetDialog
    }

    /**
     * 弹出询问是否覆盖恢复数据对话框
     */
    private fun askRestore(type: String, dialog: MaterialDialog) {
        when (type) {
            WebDavViewModel.TYPE_APP_DATABASE_DIR -> {
                dialog.message(res = R.string.restore_app_database_warning).show()
            }
        }
    }

    /**
     * 弹出询问是否删除对话框
     */
    private fun askDelete(type: String, dialog: MaterialDialog) {
        when (type) {
            WebDavViewModel.TYPE_APP_DATABASE_DIR -> {
                dialog.message(res = R.string.delete_remote_file_warning).show()
            }
        }
    }
}
