package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.arialyy.aria.core.download.DownloadEntity
import com.skyd.imomoe.bean.AnimeDownload1Bean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow


class DownloadManagerViewModel : ViewModel() {
    val downloadDataList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)

    fun initList(
        notCompleteList: List<DownloadEntity>,
        animeTitleEpisodeMap: HashMap<String, Pair<String, String>>
    ) {
        downloadDataList.tryEmit(DataState.Refreshing)
        request(request = {
            val dataList = ArrayList<AnimeDownload1Bean>()
            notCompleteList.forEach { entity ->
                val p = animeTitleEpisodeMap[entity.url]
                if (p != null) {
                    // 首次初始化列表
                    dataList.add(
                        AnimeDownload1Bean.create(
                            title = p.first,
                            episode = p.second,
                            entity = entity
                        )
                    )
                }
            }
            dataList
        }, success = {
            downloadDataList.tryEmit(DataState.Success(it))
        }, error = {
            downloadDataList.tryEmit(DataState.Error(it.message.orEmpty()))
            it.message?.showToast()
        })
    }

    fun onTaskPreStart(
        entity: DownloadEntity,
        animeTitleEpisodeMap: HashMap<String, Pair<String, String>>
    ) {
        request(request = {
            var contain = false
            val dataList = downloadDataList.value.readOrNull().orEmpty().toMutableList()
            // 根据url和下载任务id查找是否已经添加
            dataList.forEach {
                if (it is AnimeDownload1Bean) {
                    if (it.url == entity.url && it.id == entity.id) {
                        contain = true
                        return@forEach
                    }
                } else if (it is DownloadEntity) {
                    if (it.url == entity.url && it.id == entity.id) {
                        contain = true
                        return@forEach
                    }
                }
            }
            // 没有添加则添加
            if (!contain) {
                dataList.apply {
                    val p = animeTitleEpisodeMap[entity.url] ?: return@apply
                    add(
                        AnimeDownload1Bean.create(
                            title = p.first,
                            episode = p.second,
                            entity = entity
                        )
                    )
                }
            }
            dataList
        }, success = {
            downloadDataList.tryEmit(DataState.Success(it))
        }, error = {
            it.message?.showToast()
        })
    }

    fun onTaskRunning(entity: DownloadEntity) {
        request(request = {
            var dataList = downloadDataList.value.readOrNull().orEmpty()
            dataList = dataList.toMutableList().map {
                var result: Any = it
                if (it is AnimeDownload1Bean && it.url == entity.url) {
                    // 若最新数据有变化，则new一个新的bean替换之前的bean
                    // 注意：此处必须要new，不能直接更改之前的bean，否则Diff检测不出差异（旧数据被更改）
                    if (it != entity) {
                        result = AnimeDownload1Bean.create(it.title, it.episode, entity = entity)
                    }
                }
                result
            }
            dataList
        }, success = {
            downloadDataList.tryEmit(DataState.Success(it))
        })
        // onTaskRunning Toast显示错误体验不佳
    }

    fun onTaskComplete(entity: DownloadEntity) {
        request(request = {
            var dataList = downloadDataList.value.readOrNull().orEmpty()
            dataList = dataList.filter {
                if (it is AnimeDownload1Bean) {
                    // 如果是下载完成任务的id，则从list中移除
                    it.id != entity.id
                } else true
            }
            dataList
        }, success = {
            downloadDataList.tryEmit(DataState.Success(it))
        }, error = {
            it.message?.showToast()
        })
    }

    fun onTaskCancel(entity: DownloadEntity) {
        request(request = {
            var dataList = downloadDataList.value.readOrNull().orEmpty()
            dataList = dataList.filter {
                // 如果是取消任务的id，则从list中移除
                if (it is AnimeDownload1Bean) {
                    it.id != entity.id
                } else true
            }
            dataList
        }, success = {
            downloadDataList.tryEmit(DataState.Success(it))
        }, error = {
            it.message?.showToast()
        })
    }
}