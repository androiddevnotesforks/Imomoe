package com.skyd.imomoe.view.adapter.variety

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.skyd.skin.SkinManager
import java.lang.reflect.ParameterizedType

class VarietyAdapter(
    private var proxyList: MutableList<Proxy<*, *>> = mutableListOf(),
    var dataList: MutableList<Any> = mutableListOf()
) : RecyclerView.Adapter<ViewHolder>() {

    var action: ((Any?) -> Unit)? = null
    var onAttachedToRecyclerView: ((recyclerView: RecyclerView) -> Unit)? = null
    var onDetachedFromRecyclerView: ((recyclerView: RecyclerView) -> Unit)? = null
    var onFailedToRecycleView: ((holder: ViewHolder) -> Boolean)? = null
    var onViewAttachedToWindow: ((holder: ViewHolder) -> Unit)? = null
    var onViewDetachedFromWindow: ((holder: ViewHolder) -> Unit)? = null
    var onViewRecycled: ((holder: ViewHolder) -> Unit)? = null

    fun <T, VH : ViewHolder> addProxy(proxy: Proxy<T, VH>) {
        proxyList.add(proxy)
    }

    fun <T, VH : ViewHolder> removeProxy(proxy: Proxy<T, VH>) {
        proxyList.remove(proxy)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        onViewAttachedToWindow?.invoke(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        onViewDetachedFromWindow?.invoke(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        onAttachedToRecyclerView?.invoke(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        onDetachedFromRecyclerView?.invoke(recyclerView)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        onViewRecycled?.invoke(holder)
    }

    override fun onFailedToRecycleView(holder: ViewHolder): Boolean {
        return onFailedToRecycleView?.invoke(holder) ?: super.onFailedToRecycleView(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        if (viewType == -1) return EmptyViewHolder(View(parent.context))
        return proxyList[viewType].onCreateViewHolder(parent, viewType)
            .apply { SkinManager.setSkin(itemView) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        SkinManager.applyViews(holder.itemView)
        val type = getItemViewType(position)
        if (type != -1) (proxyList[type] as Proxy<Any, ViewHolder>)
            .onBindViewHolder(holder, dataList[position], position, action)
    }

    // 布局刷新
    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val type = getItemViewType(position)
        if (type != -1) (proxyList[type] as Proxy<Any, ViewHolder>)
            .onBindViewHolder(holder, dataList[position], position, action, payloads)
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return getProxyIndex(dataList[position])
    }

    // 获取策略在列表中的索引，可能返回-1
    private fun getProxyIndex(data: Any): Int = proxyList.indexOfFirst {
        // 如果Proxy<T,VH>中的第一个类型参数T和数据的类型相同，则返回对应策略的索引
        (it.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0].let { argument ->
            if (argument.toString() == data.javaClass.toString())
                true    // 正常情况
            else {
                // Proxy第一个泛型是类似List<T>，又嵌套了个泛型
                if (argument is ParameterizedType)
                    argument.rawType.toString() == data.javaClass.toString()
                else false
            }
        }
    }

    // 抽象策略类
    abstract class Proxy<T, VH : ViewHolder> {
        abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        abstract fun onBindViewHolder(
            holder: VH,
            data: T,
            index: Int,
            action: ((Any?) -> Unit)? = null
        )

        open fun onBindViewHolder(
            holder: VH,
            data: T,
            index: Int,
            action: ((Any?) -> Unit)? = null,
            payloads: MutableList<Any>
        ) {
            onBindViewHolder(holder, data, index, action)
        }
    }
}
