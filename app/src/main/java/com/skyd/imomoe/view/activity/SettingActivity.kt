package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.skyd.imomoe.databinding.ActivitySettingBinding
import com.skyd.imomoe.ext.fixKeyboardFitsSystemWindows
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.tbSettingActivity.apply {
            fixKeyboardFitsSystemWindows()
            setNavigationOnClickListener { finish() }
        }
    }

    override fun getBinding() = ActivitySettingBinding.inflate(layoutInflater)
}
