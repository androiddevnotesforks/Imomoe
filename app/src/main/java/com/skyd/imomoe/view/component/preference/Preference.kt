package com.skyd.imomoe.view.component.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.skyd.imomoe.R
import com.skyd.skin.SkinManager
import com.skyd.skin.core.attrs.SrcAttr


open class Preference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    var tvText1: TextView? = null
    var imageView: ImageView? = null

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

        if (imageView == null) {
            imageView = (holder.findViewById(android.R.id.icon) as? ImageView)?.also {
                val field = Preference::class.java.getDeclaredField("mIconResId")
                field.isAccessible = true
                SkinManager.setCustomViewAttrs(it,
                    SrcAttr().apply { attrResourceRefId = field.getInt(this@Preference) }
                )
            }
        }
        if (tvText1 == null) tvText1 = holder.findViewById(android.R.id.text1) as? TextView
        tvText1?.let {
            if (needUpdateText1) it.text = text1
            needUpdateText1 = false
        }

        SkinManager.applyViews(holder.itemView)
    }
}