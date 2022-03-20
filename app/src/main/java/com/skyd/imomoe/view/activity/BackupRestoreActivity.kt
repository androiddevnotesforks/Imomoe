package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityBackupRestoreBinding
import com.skyd.imomoe.ext.warningDialog
import com.skyd.imomoe.view.fragment.WebDavFragment

class BackupRestoreActivity : BaseActivity<ActivityBackupRestoreBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        warningDialog(
            onPositive = {
                if (savedInstanceState == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_backup_restore_activity, WebDavFragment()).commit()
                }
            },
            onNegative = { finish() },
            cancelable = false
        ).message(text = "测试功能，可能会导致本地数据遭到破坏。点击“确定”继续使用，点击“取消”关闭").show()

        mBinding.atbBackupRestoreActivity.setBackButtonClickListener { finish() }
    }

    override fun getBinding() = ActivityBackupRestoreBinding.inflate(layoutInflater)
}