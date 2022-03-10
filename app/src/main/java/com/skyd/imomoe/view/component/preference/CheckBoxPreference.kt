package com.skyd.imomoe.view.component.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.skyd.imomoe.R
import com.skyd.skin.SkinManager
import com.skyd.skin.core.attrs.ButtonTintAttr
import com.skyd.skin.core.attrs.SrcAttr


class CheckBoxPreference(context: Context, attrs: AttributeSet) :
    CheckBoxPreference(context, attrs) {
    var imageView: ImageView? = null
    var checkbox: CheckBox? = null
    var tvText1: TextView? = null

    private var needUpdateText1 = false
    var text1: CharSequence? = null
        set(value) {
            if (tvText1 == null) needUpdateText1 = true
            else tvText1?.text = value
            field = value
        }

    init {
        layoutResource = R.layout.layout_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        tvText1 = holder.findViewById(android.R.id.text1) as? TextView
        tvText1?.let {
            if (needUpdateText1) it.text = text1
            needUpdateText1 = false
        }

        if (imageView == null) {
            imageView = (holder.findViewById(android.R.id.icon) as? ImageView)?.also {
                val field = Preference::class.java.getDeclaredField("mIconResId")
                field.isAccessible = true
                SkinManager.setCustomViewAttrs(it,
                    SrcAttr().apply { attrResourceRefId = field.getInt(this@CheckBoxPreference) }
                )
            }
        }

        if (checkbox == null) {
            checkbox = (holder.findViewById(android.R.id.checkbox) as? CheckBox)?.also {
                SkinManager.setCustomViewAttrs(it,
                    ButtonTintAttr().apply {
                        attrResourceRefId = R.color.foreground_main_color_2_skin
                    }
                )
            }
        }

        SkinManager.applyViews(holder.itemView)
    }
}