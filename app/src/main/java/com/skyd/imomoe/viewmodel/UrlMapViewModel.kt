package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.database.entity.UrlMapEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class UrlMapViewModel @Inject constructor() : ViewModel() {
    var urlMapList = MutableStateFlow<DataState<List<UrlMapEntity>>>(DataState.Empty)

    init {
        getUrlMapList()
    }

    fun getUrlMapList() {
        urlMapList.value = DataState.Refreshing
        request(request = { getAppDataBase().urlMapDao().getAll() }, success = {
            urlMapList.value = DataState.Success(it)
        }, error = {
            urlMapList.value = DataState.Error(it.message.orEmpty())
        })
    }

    fun setUrlMap(oldUrl: String, newUrl: String) {
        request(request = {
            getAppDataBase().urlMapDao().setNewUrl(oldUrl, newUrl, true)
        }, success = {
            urlMapList.value = DataState.Success(
                urlMapList.value.readOrNull().orEmpty().toMutableList().apply {
                    var i = -1
                    run {
                        forEachIndexed { index, urlMapEntity ->
                            if (urlMapEntity.oldUrl == oldUrl) {
                                i = index
                                return@run
                            }
                        }
                    }
                    if (i != -1) {
                        removeAt(i)
                        add(i, UrlMapEntity(oldUrl = oldUrl, newUrl = newUrl, enabled = true))
                    } else {
                        add(UrlMapEntity(oldUrl = oldUrl, newUrl = newUrl, enabled = true))
                    }
                }
            )
        }, error = {
            it.message?.showToast()
        })
    }

    fun deleteUrlMap(oldUrl: String) {
        request(request = {
            getAppDataBase().urlMapDao().delete(oldUrl)
        }, success = {
            urlMapList.value = DataState.Success(urlMapList.value.readOrNull().orEmpty().filter {
                it.oldUrl != oldUrl
            })
        }, error = {
            it.message?.showToast()
        })
    }

    fun editUrlMap(old: Pair<String, String>, new: Pair<String, String>) {
        request(request = {
            getAppDataBase().urlMapDao().delete(old.first)
            getAppDataBase().urlMapDao().setNewUrl(new.first, new.second, true)
        }, success = {
            urlMapList.value = DataState.Success(
                urlMapList.value.readOrNull().orEmpty().toMutableList().apply {
                    var i = -1
                    run {
                        forEachIndexed { index, urlMapEntity ->
                            if (urlMapEntity.oldUrl == old.first && urlMapEntity.newUrl == old.second) {
                                i = index
                                return@run
                            }
                        }
                    }
                    if (i != -1) {
                        removeAt(i)
                    }
                    add(UrlMapEntity(oldUrl = new.first, newUrl = new.second, enabled = true))
                }
            )
        }, error = {
            it.message?.showToast()
        })
    }

    fun enabledUrlMap(oldUrl: String, enable: Boolean) {
        request(request = {
            getAppDataBase().urlMapDao().enabled(oldUrl, enable)
        }, success = {
            urlMapList.value = DataState.Success(urlMapList.value.readOrNull().orEmpty().apply {
                forEach {
                    if (it.oldUrl == oldUrl) {
                        it.enabled = enable
                        return@apply
                    }
                }
            })
        }, error = {
            it.message?.showToast()
        })
    }
}