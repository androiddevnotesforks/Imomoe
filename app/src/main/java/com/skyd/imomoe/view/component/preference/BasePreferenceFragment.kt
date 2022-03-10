package com.skyd.imomoe.view.component.preference

import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import com.skyd.skin.core.SkinBasePreferenceFragment
import org.greenrobot.eventbus.EventBus


abstract class BasePreferenceFragment : SkinBasePreferenceFragment() {
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
