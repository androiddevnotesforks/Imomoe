package com.skyd.imomoe.view.component.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.skyd.imomoe.R
import com.skyd.skin.SkinManager

class PreferenceCategory(context: Context, attrs: AttributeSet?) :
    PreferenceCategory(context, attrs) {

    init {
        layoutResource = R.layout.layout_preference_category_material
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        SkinManager.applyViews(holder.itemView)
    }
}