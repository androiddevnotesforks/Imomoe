package com.skyd.imomoe.util.skin.attrs

import android.view.View
import androidx.core.content.ContextCompat
import com.skyd.imomoe.view.component.VpSwipeRefreshLayout
import com.skyd.skin.core.SkinResourceProcessor
import com.skyd.skin.core.attrs.SkinAttr


class ColorSchemeAttr : SkinAttr() {
    override fun applySkin(view: View) {
        if (view is VpSwipeRefreshLayout && attrResourceRefId != -1) {
            val skinResProcessor = SkinResourceProcessor.instance
            if (skinResProcessor.usingDefaultSkin() && skinResProcessor.usingInnerAppSkin()) {
                view.setColorSchemeColors(ContextCompat.getColor(view.context, attrResourceRefId))
            } else {
                view.setColorSchemeColors(skinResProcessor.getColor(attrResourceRefId))
            }
        }
    }

    override fun tag(): String = "colorScheme"
}