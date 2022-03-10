package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivitySettingBinding
import com.skyd.imomoe.view.fragment.SettingFragment


class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            atbSettingActivityToolbar.setBackButtonClickListener { finish() }

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, SettingFragment())
                .commit()
        }
    }

    override fun getBinding(): ActivitySettingBinding =
        ActivitySettingBinding.inflate(layoutInflater)
}
