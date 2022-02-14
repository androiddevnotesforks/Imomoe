package com.skyd.imomoe.model.impls.custom

import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Api
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.ArrayList
import java.net.URL

object ParseHtmlUtil {

    fun parseHeroWrap(element: Element, imageReferer: String): List<AnimeCover6Bean> {      //banner
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
                    url, title, ImageBean("", cover, imageReferer), describe,
                    AnimeEpisodeDataBean("", episodeTitle)
                )
            )
        }
        return list
    }

    fun parseTers(element: Element): List<ClassifyBean> {
        val list: MutableList<ClassifyBean> = ArrayList()
        val pElements: Elements = element.select("p")
        for (i in pElements.indices) {
            val pChildren: Elements = pElements[i].children()
            for (j in pChildren.indices) {
                when (pChildren[j].tagName()) {
                    "label" -> {
                        list.add(
                            ClassifyBean("", pChildren[j].text(), ArrayList())
                        )
                    }
                    "a" -> {
                        if (list.size > 0) {
                            list[list.size - 1].classifyDataList.add(
                                ClassifyTab1Bean(
                                    pChildren[j].attr("href"),
                                    Api.MAIN_URL + pChildren[j].attr("href"),
                                    pChildren[j].text()
                                )
                            )
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
                        url, Api.MAIN_URL + url, title,
                        AnimeEpisodeDataBean(episodeUrl, episodeTitle)
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
                        AnimeEpisodeDataBean(episodeUrl, episodeTitle),
                    )
                )
            }
            ulList.add(liList)
        }
        return ulList
    }

    fun parseTopli2(element: Element): List<AnimeCover11Bean> {
        val animeShowList: MutableList<AnimeCover11Bean> = ArrayList()
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
            animeShowList.add(
                AnimeCover11Bean(actionUrl = url, url = Api.MAIN_URL + url, title = title)
            )
        }
        return animeShowList
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
                    actionUrl = url, url = Api.MAIN_URL + url,
                    title = title,
                    area = AnimeAreaBean(areaUrl, Api.MAIN_URL + areaUrl, areaTitle),
                    episodeClickable = AnimeEpisodeDataBean(episodeUrl, episodeTitle),
                    date = date
                )
            )
        }
        return animeShowList
    }

    fun parseDnews2(element: Element, imageReferer: String): List<AnimeCover4Bean> {
        val animeShowList: MutableList<AnimeCover4Bean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = elements[i].select("p").select("a").text()
            animeShowList.add(
                AnimeCover4Bean(
                    actionUrl = url, url = Api.MAIN_URL + url,
                    title = title, cover = ImageBean("", cover, imageReferer)
                )
            )
        }
        return animeShowList
    }

    fun parsePics2(element: Element, imageReferer: String): List<AnimeCover3Bean> {      //一周动漫排行榜
        val animeCover3List: MutableList<AnimeCover3Bean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            val cover = results[i].select("a")
                .select("img").attr("src")
            val title = results[i].select("h2")
                .select("a").text()
            val url = results[i].select("h2")
                .select("a").attr("href")
            val episode = results[i].select("span")
                .select("font").text()
            val types = results[i].select("span")[1].select("a")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                animeType.add(
                    AnimeTypeBean(
                        types[j].attr("href"),
                        Api.MAIN_URL + types[j].attr("href"),
                        types[j].text()
                    )
                )
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCover3Bean(
                    actionUrl = url,
                    url = Api.MAIN_URL + url,
                    title = title,
                    cover = ImageBean("", cover, imageReferer),
                    episode = episode,
                    animeType = animeType,
                    describe = describe
                )
            )
        }
        return animeCover3List
    }

    fun parseLpic(element: Element, imageReferer: String): List<AnimeCover3Bean> {
        val animeCover3List: MutableList<AnimeCover3Bean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = results[i].select("h2").select("a").attr("title")
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].select("a")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                animeType.add(
                    AnimeTypeBean(
                        types[j].attr("href"),
                        Api.MAIN_URL + types[j].attr("href"),
                        types[j].text()
                    )
                )
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCover3Bean(
                    actionUrl = url,
                    url = Api.MAIN_URL + url,
                    title = title,
                    cover = ImageBean("", cover, imageReferer),
                    episode = episode,
                    animeType = animeType,
                    describe = describe
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
            if (findCurrentPage) {
                if (results[i].className() == "a1") return null
                val url = results[i].attr("href")
                val title = results[i].text()
                return PageNumberBean(url, Api.MAIN_URL + url, title)
            }
            if (results[i].tagName() == "span") findCurrentPage = true
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
        element: Element,
        selected: AnimeEpisodeDataBean? = null
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

    fun parseImg(
        element: Element,
        imageReferer: String
    ): List<AnimeCover1Bean> {
        val animeShowList: MutableList<AnimeCover1Bean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = elements[i].select("[class=tname]").select("a").text()
            var episode = ""
            if (elements[i].select("p").size > 1) {
                episode = elements[i].select("p")[1].select("a").text()
            }
            animeShowList.add(
                AnimeCover1Bean(
                    actionUrl = url, url = Api.MAIN_URL + url,
                    title = title, cover = ImageBean("", cover, imageReferer),
                    episode = episode
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