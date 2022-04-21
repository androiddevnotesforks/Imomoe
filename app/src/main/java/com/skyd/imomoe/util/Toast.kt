package com.skyd.imomoe.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext

private var uiThreadHandler: Handler = Handler(Looper.getMainLooper())

fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    uiThreadHandler.post {
        val toast = Toast(appContext)
        val view: View =
            (appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.toast_1, null)
        view.findViewById<TextView>(R.id.tv_toast_1).also {
            it.text = this
            it.setTextColor(ContextCompat.getColor(appContext, R.color.on_primary_pink))
        }
        view.setBackgroundResource(R.drawable.shape_fill_circle_corner_50)
        toast.view = view
        toast.duration = duration
        toast.show()
    }
}
