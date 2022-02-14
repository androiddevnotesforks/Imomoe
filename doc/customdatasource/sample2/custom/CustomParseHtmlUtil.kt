package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Api
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URL

object CustomParseHtmlUtil {

    fun parseHeroWrap(element: Element, imageReferer: String): List<AnimeCover6Bean> {//banner
        val list: MutableList<AnimeCover6Bean> = ArrayList()
        val liElements: Elements = element.select("[class=heros]").select("li")
        for (i in liElements.indices) {
            var episodeTitle = ""
            var title = ""
            var describe = ""
            var url = ""
            var cover = ""
            val liChildren: Elements = liElements[i].children()
            for (j in liChildren.indices) {
                when (liChildren[j].tagName()) {
                    "a" -> {
                        url = liChildren[j].attr("href")
                        cover = liChildren[j].select("img").attr("src")
                        title = liChildren[j].select("p").first()!!.ownText()
                        describe = liChildren[j].select("p").select("span").text()
                    }
                    "em" -> {
                        episodeTitle = liChildren[j].text()
                    }
                }
            }
            list.add(
                AnimeCover6Bean(
                    url, title, ImageBean("", cover, imageReferer),
                    describe, AnimeEpisodeDataBean("", episodeTitle)
                )
            )
        }
        return list
    }

    fun parseSearchList(element: Element): List<ClassifyBean> {
        val list: MutableList<ClassifyBean> = ArrayList()
        val liElements: Elements = element.select("li")
        for (i in liElements.indices) {
            val pChildren: Elements = liElements[i].children()
            for (j in pChildren.indices) {
                when (pChildren[j].tagName()) {
                    "span" -> {
                        list.add(
                            ClassifyBean(
                                "",
                                pChildren[j].text().replace("：", ""),
                                ArrayList()
                            )
                        )
                    }
                    "ul" -> {
                        if (list.size > 0) {
                            val li = pChildren[j].select("li")
                            val classifyDataList = list[list.size - 1].classifyDataList
                            for (l in li.indices) {
                                val a = li[l].select("a")
                                classifyDataList.add(
                                    ClassifyTab1Bean(
                                        a.attr("href").replace(Api.MAIN_URL, ""),
                                        a.attr("href"),
                                        a.text()
                                    )
                                )
                            }
                        }
                    }
                }
            }


        }
        return list
    }

    fun parseTlist(element: Element): List<List<AnimeCover10Bean>> {
        val ulList: MutableList<List<AnimeCover10Bean>> = ArrayList()
        val ulElements: Elements = element.select("ul")
        for (i in ulElements.indices) {
            val liList: MutableList<AnimeCover10Bean> = ArrayList()
            val liElements: Elements = ulElements[i].select("li")
            for (j in liElements.indices) {
                val episodeTitle = liElements[j].select("span").select("a").text()
                val title = liElements[j].select("a")[1].text()
                val url = liElements[j].select("a")[1].attr("href")
                val episodeUrl = liElements[j].select("span").select("a").attr("href")
                liList.add(
                    AnimeCover10Bean(
                        url, Api.MAIN_URL + url,
                        title, AnimeEpisodeDataBean("", episodeTitle, episodeUrl)
                    )
                )
            }
            ulList.add(liList)
        }
        return ulList
    }

    fun parseTlist2(element: Element): List<List<AnimeCover12Bean>> {
        val ulList: MutableList<List<AnimeCover12Bean>> = ArrayList()
        val ulElements: Elements = element.select("ul")
        for (i in ulElements.indices) {
            val liList: MutableList<AnimeCover12Bean> = ArrayList()
            val liElements: Elements = ulElements[i].select("li")
            for (j in liElements.indices) {
                val episodeTitle = liElements[j].select("span").select("a").text()
                val title = liElements[j].select("a")[1].text()
                val url = liElements[j].select("a")[1].attr("href")
                val episodeUrl = liElements[j].select("span").select("a").attr("href")
                liList.add(
                    AnimeCover12Bean(
                        url, Api.MAIN_URL + url, title,
                        AnimeEpisodeDataBean("", episodeTitle, episodeUrl)
                    )
                )
            }
            ulList.add(liList)
        }
        return ulList
    }

    fun parseTopli(element: Element): List<AnimeCover5Bean> {
        val animeShowList: MutableList<AnimeCover5Bean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            var url: String
            var title: String
            if (elements[i].select("a").size >= 2) {    //最近更新，显示地区的情况
                url = elements[i].select("a")[1].attr("href")
                title = elements[i].select("a")[1].text()
                if (elements[i].select("span")[0].children().size == 0) {     //最近更新，不显示地区的情况
                    url = elements[i].select("a")[0].attr("href")
                    title = elements[i].select("a")[0].text()
                }
            } else {                                            //总排行榜
                url = elements[i].select("a")[0].attr("href")
                title = elements[i].select("a")[0].text()
            }

            val areaUrl = elements[i].select("span").select("a")
                .attr("href")
            val areaTitle = elements[i].select("span").select("a").text()
            var episodeUrl = elements[i].select("b").select("a")
                .attr("href")
            val episodeTitle = elements[i].select("b").select("a").text()
            val date = elements[i].select("em").text()
            if (episodeUrl == "") {
                episodeUrl = url
            }
            animeShowList.add(
                AnimeCover5Bean(
                    url, Api.MAIN_URL + url,
                    title, AnimeAreaBean(areaUrl, Api.MAIN_URL + areaUrl, areaTitle),
                    date, AnimeEpisodeDataBean("", episodeTitle, episodeUrl),
                )
            )
        }
        return animeShowList
    }

    fun parseDnews(element: Element, imageReferer: String): List<AnimeCover4Bean> {
        val animeShowList: MutableList<AnimeCover4Bean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(cover, imageReferer)
            val title = elements[i].select("p").select("a").text()
            animeShowList.add(
                AnimeCover4Bean(
                    url, Api.MAIN_URL + url, title,
                    ImageBean("", cover, imageReferer)
                )
            )
        }
        return animeShowList
    }

    fun parsePics(element: Element, imageReferer: String): List<AnimeCover3Bean> {//一周动漫排行榜
        val animeCover3List: MutableList<AnimeCover3Bean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            val cover = getCoverUrl(
                results[i].select("a").select("img").attr("src"),
                imageReferer
            )
            val title = results[i].select("h2").select("a").text()
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].text().replace("类型：", "")
                .replace("类型:", "").split(" ")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                if (types[j].isBlank()) continue
                animeType.add(AnimeTypeBean("", "", types[j]))
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCover3Bean(
                    url,
                    Api.MAIN_URL + url,
                    title,
                    ImageBean("", cover, imageReferer),
                    episode,
                    describe,
                    animeType
                )
            )
        }
        return animeCover3List
    }

    fun parseRankListLpic(element: Element, imageReferer: String): List<AnimeCover3Bean> {//排行榜
        val animeCover3List: MutableList<AnimeCover3Bean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = getCoverUrl(cover, imageReferer)
            val title = results[i].select("h2").select("a").attr("title")
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[2].text().replace("类型：", "")
                .replace("类型:", "").split(" ")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                if (types[j].isBlank()) continue
                animeType.add(AnimeTypeBean("", "", types[j]))
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCover3Bean(
                    url,
                    Api.MAIN_URL + url,
                    title,
                    ImageBean("", cover, imageReferer),
                    episode,
                    describe,
                    animeType
                )
            )
        }
        return animeCover3List
    }

    fun parseLpic(element: Element, imageReferer: String): List<AnimeCover3Bean> {          //搜索
        val animeCover3List: MutableList<AnimeCover3Bean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = getCoverUrl(cover, imageReferer)
            val title = results[i].select("h2").select("a").attr("title")
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].text().replace("类型：", "")
                .replace("类型:", "").split(" ")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                if (types[j].isBlank()) continue
                animeType.add(AnimeTypeBean("", "", types[j]))
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCover3Bean(
                    url,
                    Api.MAIN_URL + url,
                    title,
                    ImageBean("", cover, imageReferer),
                    episode,
                    describe,
                    animeType
                )
            )
        }
        return animeCover3List
    }

    /**
     * 只获取下一页的地址，没有下一页则返回null
     */
    fun parseNextPages(element: Element): PageNumberBean? {
        val results: Elements = element.children()
        var findCurrentPage = false
        for (i in results.indices) {
            if (results[i].tagName() == "a" && i > 0 && results[i - 1].tagName() == "span")
                findCurrentPage = true
            if (findCurrentPage) {
                val url = results[i].attr("href")
                val title = results[i].text()
                return PageNumberBean(url, Api.MAIN_URL + url, title)
            }
        }
        return null
    }

    fun parseDtit(element: Element): String {
        return element.children()[0].text()
    }

    fun parseBotit(element: Element): String {
        return element.select("h2").text()
    }

    fun parseMovurls(
        element: Element, selected: AnimeEpisodeDataBean? = null,
    ): List<AnimeEpisodeDataBean> {
        val animeEpisodeList: MutableList<AnimeEpisodeDataBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (k in elements.indices) {
            if (selected != null && elements[k].className() == "sel") {
                selected.title = elements[k].select("a").text()
                selected.actionUrl = elements[k].select("a").attr("href")
            }
            animeEpisodeList.add(
                AnimeEpisodeDataBean(
                    elements[k].select("a").attr("href"),
                    elements[k].select("a").text()
                )
            )
        }
        return animeEpisodeList
    }

    fun parseImg(element: Element, imageReferer: String): List<AnimeCover1Bean> {
        val animeShowList: MutableList<AnimeCover1Bean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(cover, imageReferer)
            val title = elements[i].select("[class=tname]").select("a").text()
            var episode = ""
            if (elements[i].select("p").size > 1) {
                episode = elements[i].select("p")[1].select("a").text()
            }
            animeShowList.add(
                AnimeCover1Bean(
                    url, Api.MAIN_URL + url,
                    title, ImageBean("", cover, imageReferer), episode
                )
            )
        }
        return animeShowList
    }

    fun getCoverUrl(cover: String, imageReferer: String): String {
        return when {
            cover.startsWith("//") -> {
                try {
                    "${URL(imageReferer).protocol}:$cover"
                } catch (e: Exception) {
                    e.printStackTrace()
                    cover
                }
            }
            cover.startsWith("/") -> {
                //url不全的情况
                Api.MAIN_URL + cover
            }
            else -> cover
        }
    }
}