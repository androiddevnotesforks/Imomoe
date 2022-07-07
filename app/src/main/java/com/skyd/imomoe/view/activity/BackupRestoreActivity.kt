package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.skyd.imomoe.databinding.ActivityBackupRestoreBinding
import com.skyd.imomoe.ext.addFitsSystemWindows

class BackupRestoreActivity : BaseActivity<ActivityBackupRestoreBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.ablBackupRestoreActivity.addFitsSystemWindows(right = true, top = true)
        mBinding.tbBackupRestoreActivity.setNavigationOnClickListener { finish() }
    }

    override fun getBinding() = ActivityBackupRestoreBinding.inflate(layoutInflater)
}