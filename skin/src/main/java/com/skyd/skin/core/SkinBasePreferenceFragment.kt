package com.skyd.skin.core

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.skyd.skin.SkinManager
import com.skyd.skin.core.listeners.ChangeSkinListener

abstract class SkinBasePreferenceFragment : PreferenceFragmentCompat(), ChangeSkinListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SkinManager.addListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SkinManager.removeListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        listView.adapter?.notifyDataSetChanged()
    }

}