package com.skyd.imomoe.view.component.preference

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.skyd.imomoe.R
import com.skyd.skin.SkinManager
import com.skyd.skin.core.attrs.SrcAttr


open class Preference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    private var imageViewSrcAttr: SrcAttr = SrcAttr()
    private var textView: TextView? = null
    var text1: CharSequence? = null
        set(value) {
            textView?.text = value
            field = value
        }

    init {
        layoutResource = R.layout.layout_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        (holder.findViewById(android.R.id.icon) as? ImageView)?.also {
            val field = Preference::class.java.getDeclaredField("mIconResId")
            field.isAccessible = true
            val resInt = field.getInt(this)
            imageViewSrcAttr.attrResourceRefId = if (resInt == 0) -1 else resInt
            SkinManager.setCustomViewAttrs(it, imageViewSrcAttr)
        }
        textView = (holder.findViewById(android.R.id.text1) as? TextView)?.also {
            it.text = text1
        }

        SkinManager.applyViews(holder.itemView)
    }
}