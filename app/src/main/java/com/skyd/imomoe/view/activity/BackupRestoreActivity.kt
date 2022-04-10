package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.skyd.imomoe.databinding.ActivityBackupRestoreBinding
import com.skyd.imomoe.ext.fixKeyboardFitsSystemWindows

class BackupRestoreActivity : BaseActivity<ActivityBackupRestoreBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.tbBackupRestoreActivity.apply {
            fixKeyboardFitsSystemWindows()
            setNavigationOnClickListener { finish() }
        }
    }

    override fun getBinding() = ActivityBackupRestoreBinding.inflate(layoutInflater)
}