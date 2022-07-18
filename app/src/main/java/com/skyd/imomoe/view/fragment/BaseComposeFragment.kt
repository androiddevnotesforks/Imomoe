package com.skyd.imomoe.view.fragment

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import org.greenrobot.eventbus.EventBus


abstract class BaseComposeFragment : Fragment() {
    protected var isFirstLoadData = true

    fun setContentBase(content: @Composable () -> Unit): View =
        ComposeView(requireContext()).apply {
            setContent {
                Mdc3Theme {
                    content.invoke()
                }
            }
        }

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
