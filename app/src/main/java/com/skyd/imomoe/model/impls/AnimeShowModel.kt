package com.skyd.imomoe.model.impls

import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.processor.ConfigDataSourceActivityProcessor
import com.skyd.imomoe.route.processor.OpenBrowserProcessor

class AnimeShowModel : IAnimeShowModel {
    override suspend fun getAnimeShowData(
        partUrl: String
    ): Pair<ArrayList<Any>, PageNumberBean?> {
        return if (partUrl == "/market") {
            Pair(
                arrayListOf(
                    Header1Bean(
                        route = "",
                        title = "使用方法"
                    ),
                    AnimeCover3Bean(
                        route = ConfigDataSourceActivityProcessor.route.buildRouteUri {
                            appendQueryParameter("selectPageIndex", "1")
                        }.toString(),
                        url = "",
                        title = "点击这里进入数据源商店",
                        cover = ImageBean(url = R.drawable.ic_new_use_data_source_step_to_market.toString()),
                        describe = "数据源商店需要访问GitHub，因此网络连接可能会过慢甚至无法访问，建议使用科学方法或配置URL前缀转换",
                        animeType = listOf(AnimeTypeBean(title = "步骤1"))
                    ),
                    AnimeCover1Bean(
                        route = "",
                        url = "",
                        title = "在商店页面点击下载按钮",
                        cover = ImageBean(url = R.drawable.ic_new_use_data_source_step_click_download.toString()),
                        episode = "步骤2"
                    ),
                    AnimeCover1Bean(
                        route = "",
                        url = "",
                        title = "等待下载完成",
                        cover = ImageBean(url = R.drawable.ic_new_use_data_source_step_downloaded.toString()),
                        episode = "步骤3"
                    ),
                    AnimeCover1Bean(
                        route = ConfigDataSourceActivityProcessor.route.buildRouteUri {
                            appendQueryParameter("selectPageIndex", "0")
                        }.toString(),
                        url = "",
                        title = "滑动到左侧页面，或点击这里",
                        cover = ImageBean(url = R.drawable.ic_new_use_data_source_step_left_page.toString()),
                        episode = "步骤4"
                    ),
                    AnimeCover1Bean(
                        route = ConfigDataSourceActivityProcessor.route.buildRouteUri {
                            appendQueryParameter("selectPageIndex", "0")
                        }.toString(),
                        url = "",
                        title = "点击要使用的数据源",
                        cover = ImageBean(url = R.drawable.ic_new_use_data_source_step_use.toString()),
                        episode = "步骤5"
                    ),
                ), null
            )
        } else Pair(
            arrayListOf(
                Banner1Bean(
                    "",
                    arrayListOf(
                        AnimeCover6Bean(
                            route = OpenBrowserProcessor.route.buildRouteUri {
                                appendQueryParameter(
                                    "url",
                                    "https://github.com/SkyD666/Imomoe/tree/master/doc/customdatasource/README.md"
                                )
                            }.toString(),
                            title = "请在设置页面选择自定义数据源ads包,以便使用APP",
                            cover = ImageBean(),
                            describe = "具体使用方法请点击此处"
                        )
                    )
                ),

                // 如何导入并使用自定义数据源？
                Header1Bean(
                    route = "",
                    title = "如何导入并使用自定义数据源？"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "找到ads数据源文件",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_ads_files.toString()),
                    episode = "步骤1"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "用樱花动漫打开ads文件",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_open_ads.toString()),
                    episode = "步骤2"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "确认导入数据源",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_import_dialog.toString()),
                    episode = "步骤3"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击已有的数据,选择重启APP",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_set.toString()),
                    episode = "步骤4"
                ),

                // 如何删除自定义数据源？
                Header1Bean(
                    route = "",
                    title = "如何删除自定义数据源？"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "长按要删除的项目",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_long_click.toString()),
                    episode = "步骤1"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击确定,以删除",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_delete.toString()),
                    episode = "步骤2"
                ),

                // 如何恢复默认数据源？
                Header1Bean(
                    route = "",
                    title = "如何恢复默认数据源？"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击右上角恢复按钮",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_reset_button.toString()),
                    episode = "步骤1"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击重启,以恢复",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_reset_dialog.toString()),
                    episode = "步骤2"
                ),

                // 如何进入自定义数据源界面？
                Header1Bean(
                    route = "",
                    title = "如何进入自定义数据源界面？"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击更多",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_more.toString()),
                    episode = "步骤1"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击设置",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_setting.toString()),
                    episode = "步骤2"
                ),
                AnimeCover1Bean(
                    route = "",
                    url = "",
                    title = "点击自定义数据源",
                    cover = ImageBean(url = R.drawable.ic_use_data_source_step_custom.toString()),
                    episode = "步骤3"
                )
            ), null
        )
    }
}