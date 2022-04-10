package com.skyd.imomoe.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import kotlin.math.abs

class ViewPager2View(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private val mViewPager2: ViewPager2 = ViewPager2(context, attrs)

    var adapter: RecyclerView.Adapter<*>?
        get() = mViewPager2.adapter
        set(value) {
            mViewPager2.adapter = value
        }

    var offscreenPageLimit: Int
        get() = mViewPager2.offscreenPageLimit
        set(value) {
            mViewPager2.offscreenPageLimit = value
        }

    var currentItem: Int
        get() = mViewPager2.currentItem
        set(value) {
            mViewPager2.currentItem = value
        }

    var orientation: Int
        get() = mViewPager2.orientation
        set(value) {
            mViewPager2.orientation = value
        }

    private var mStartX = 0f
    private var mStartY = 0f
    private var mTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop

    init {
        mViewPager2.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        attachViewToParent(mViewPager2, 0, mViewPager2.layoutParams)
    }

    fun getViewPager() = mViewPager2

    fun setPageTransformer(@Nullable transformer: ViewPager2.PageTransformer) {
        mViewPager2.setPageTransformer(transformer)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        mViewPager2.setCurrentItem(item, smoothScroll)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = ev.x
                mStartY = ev.y
                mViewPager2.isUserInputEnabled = true
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x
                val endY = ev.y
                val disX = abs(endX - mStartX)
                val disY = abs(endY - mStartY)
                mViewPager2.isUserInputEnabled =
                    !(disX * 0.6 < disY && mViewPager2.scrollState != SCROLL_STATE_DRAGGING)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                mViewPager2.isUserInputEnabled = true
        }
        return super.dispatchTouchEvent(ev)
    }
}