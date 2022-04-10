package com.skyd.imomoe.view.component.preference

import androidx.preference.PreferenceFragmentCompat
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import org.greenrobot.eventbus.EventBus


abstract class BasePreferenceFragment : PreferenceFragmentCompat() {
    protected var isFirstLoadData = true

    override fun onStart() {
        super.onStart()
        if (this is EventBusSubscriber) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (this is EventBusSubscriber && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }
}
