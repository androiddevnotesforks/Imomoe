package com.skyd.imomoe.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.view.component.bannerview.BannerView
import com.skyd.imomoe.view.component.FlowLayout

//UP_TODO 2022/1/22 12:31 0 ViewHolder直接使用ViewBinding
class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

class DataSource1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvDataSource1Name: TextView = view.findViewById(R.id.tv_data_source_1_name)
    val tvDataSource1Size: TextView = view.findViewById(R.id.tv_data_source_1_size)
    val ivDataSource1Selected: ImageView = view.findViewById(R.id.iv_data_source_1_selected)
}

class DataSource2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvDataSource2Name: TextView = view.findViewById(R.id.tv_data_source_2_name)
    val tvDataSource2Author: TextView = view.findViewById(R.id.tv_data_source_2_author)
    val ivDataSource2Icon: ImageView = view.findViewById(R.id.iv_data_source_2_icon)
    val tvDataSource2Describe: TextView = view.findViewById(R.id.tv_data_source_2_describe)
    val tvDataSource2PublishAt: TextView = view.findViewById(R.id.tv_data_source_2_publish_at)
    val tvDataSource2Version: TextView = view.findViewById(R.id.tv_data_source_2_version)
}

class GridRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvGridRecyclerView1: RecyclerView = view.findViewById(R.id.rv_grid_recycler_view_1)
}

class Header1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvHeader1Title: TextView = view.findViewById(R.id.tv_header_1_title)
}

class AnimeCover1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover1Cover: ImageView = view.findViewById(R.id.iv_anime_cover_1_cover)
    val tvAnimeCover1Title: TextView = view.findViewById(R.id.tv_anime_cover_1_title)
    val tvAnimeCover1Episode: TextView = view.findViewById(R.id.tv_anime_cover_1_episode)
}

class AnimeCover2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover1Title: TextView = view.findViewById(R.id.tv_anime_cover_2_title)
    val tvAnimeCover1Episode: TextView = view.findViewById(R.id.tv_anime_cover_2_episode)
}

class AnimeCover3ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover3Cover: ImageView = view.findViewById(R.id.iv_anime_cover_3_cover)
    val tvAnimeCover3Title: TextView = view.findViewById(R.id.tv_anime_cover_3_title)
    val tvAnimeCover3Episode: TextView = view.findViewById(R.id.tv_anime_cover_3_episode)
    val flAnimeCover3Type: FlowLayout = view.findViewById(R.id.fl_anime_cover_3_type)
    val tvAnimeCover3Describe: TextView = view.findViewById(R.id.tv_anime_cover_3_describe)
    val tvAnimeCover3Alias: TextView = view.findViewById(R.id.tv_anime_cover_3_alias)
}

class AnimeCover4ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover4Cover: ImageView = view.findViewById(R.id.iv_anime_cover_4_cover)
    val tvAnimeCover4Title: TextView = view.findViewById(R.id.tv_anime_cover_4_title)
}

class AnimeCover5ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover5Title: TextView = view.findViewById(R.id.tv_anime_cover_5_title)
    val tvAnimeCover5Area: TextView = view.findViewById(R.id.tv_anime_cover_5_area)
    val tvAnimeCover5Date: TextView = view.findViewById(R.id.tv_anime_cover_5_date)
    val tvAnimeCover5Episode: TextView = view.findViewById(R.id.tv_anime_cover_5_episode)
}

class AnimeCover6ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeCover6Cover: ImageView = view.findViewById(R.id.iv_anime_cover_6_cover)
    val tvAnimeCover6Title: TextView = view.findViewById(R.id.tv_anime_cover_6_title)
    val tvAnimeCover6Episode: TextView = view.findViewById(R.id.tv_anime_cover_6_episode)
    val tvAnimeCover6Describe: TextView = view.findViewById(R.id.tv_anime_cover_6_describe)
}

class AnimeCover7ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover7Title: TextView = view.findViewById(R.id.tv_anime_cover_7_title)
    val tvAnimeCover7Size: TextView = view.findViewById(R.id.tv_anime_cover_7_size)
    val tvAnimeCover7Episodes: TextView = view.findViewById(R.id.tv_anime_cover_7_episodes)
    val tvAnimeCover7OldPath: TextView = view.findViewById(R.id.tv_anime_cover_7_old_path)
}

class AnimeCover8ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover8Title: TextView = view.findViewById(R.id.tv_anime_cover_8_title)
    val tvAnimeCover8Episodes: TextView = view.findViewById(R.id.tv_anime_cover_8_episode)
    val ivAnimeCover8Cover: ImageView = view.findViewById(R.id.iv_anime_cover_8_cover)
}

class AnimeCover9ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover9Title: TextView = view.findViewById(R.id.tv_anime_cover_9_title)
    val tvAnimeCover9Episodes: TextView = view.findViewById(R.id.tv_anime_cover_9_episode)
    val tvAnimeCover9Time: TextView = view.findViewById(R.id.tv_anime_cover_9_time)
    val tvAnimeCover9DetailPage: TextView = view.findViewById(R.id.tv_anime_cover_9_detail_page)
    val ivAnimeCover9Cover: ImageView = view.findViewById(R.id.iv_anime_cover_9_cover)
    val ivAnimeCover9Delete: ImageView = view.findViewById(R.id.iv_anime_cover_9_delete)
}

class AnimeCover11ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover11Title: TextView = view.findViewById(R.id.tv_anime_cover_11_title)
    val tvAnimeCover11Rank: TextView = view.findViewById(R.id.tv_anime_cover_11_rank)
}

class AnimeCover12ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeCover12Title: TextView = view.findViewById(R.id.tv_anime_cover_12_title)
    val tvAnimeCover12Episode: TextView = view.findViewById(R.id.tv_anime_cover_12_episode)
}

class AnimeEpisodeFlowLayout1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val flAnimeEpisodeFlowLayout1: FlowLayout = view.findViewById(R.id.fl_anime_episode)
}

class AnimeEpisodeFlowLayout2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvAnimeEpisodeFlowLayout2: RecyclerView = view.findViewById(R.id.fl_anime_episode_2)
}

class AnimeDescribe1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeDescribe1: TextView = view.findViewById(R.id.tv_anime_describe)
}

class Banner1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val banner1: BannerView = view.findViewById(R.id.banner_1)
}

class LicenseHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicenseHeader1Name: TextView = view.findViewById(R.id.tv_license_header_1_name)
    val tvLicenseHeader1License: TextView = view.findViewById(R.id.tv_license_header_1_license)
}

class License1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvLicense1Name: TextView = view.findViewById(R.id.tv_license_1_name)
    val tvLicense1License: TextView = view.findViewById(R.id.tv_license_1_license)
}

class SearchHistoryHeader1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistoryHeader1Title: TextView =
        view.findViewById(R.id.tv_search_history_header_1_title)
}

class SearchHistory1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvSearchHistory1Title: TextView = view.findViewById(R.id.tv_search_history_1_title)
    val ivSearchHistory1Delete: ImageView = view.findViewById(R.id.iv_search_history_1_delete)
}

class AnimeInfo1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivAnimeInfo1Cover: ImageView = view.findViewById(R.id.iv_anime_info_1_cover)
    val tvAnimeInfo1Title: TextView = view.findViewById(R.id.tv_anime_info_1_title)
    val tvAnimeInfo1Alias: TextView = view.findViewById(R.id.tv_anime_info_1_alias)
    val tvAnimeInfo1Area: TextView = view.findViewById(R.id.tv_anime_info_1_area)
    val tvAnimeInfo1Year: TextView = view.findViewById(R.id.tv_anime_info_1_year)
    val tvAnimeInfo1Index: TextView = view.findViewById(R.id.tv_anime_info_1_index)
    val tvAnimeInfo1Type: TextView = view.findViewById(R.id.tv_anime_info_1_type)
    val flAnimeInfo1Type: FlowLayout = view.findViewById(R.id.fl_anime_info_1_type)
    val tvAnimeInfo1Tag: TextView = view.findViewById(R.id.tv_anime_info_1_tag)
    val flAnimeInfo1Tag: FlowLayout = view.findViewById(R.id.fl_anime_info_1_tag)
    val tvAnimeInfo1Info: TextView = view.findViewById(R.id.tv_anime_info_1_info)
    val tvAnimeInfoContinuePlay: TextView = view.findViewById(R.id.tv_anime_info_continue_play)
}

class HorizontalRecyclerView1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val rvHorizontalRecyclerView1: RecyclerView =
        view.findViewById(R.id.rv_horizontal_recycler_view_2)
    val ivHorizontalRecyclerView1More: ImageView =
        view.findViewById(R.id.iv_horizontal_recycler_view_2_more)
}

class AnimeEpisode1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeEpisode1: TextView = view.findViewById(R.id.tv_anime_episode_1)
}

class UpnpDevice1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvUpnpDevice1Title: TextView = view.findViewById(R.id.tv_upnp_device_1_title)
}

class More1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivMore1: ImageView = view.findViewById(R.id.iv_more_1)
    val tvMore1: TextView = view.findViewById(R.id.tv_more_1)
}

class SkinCover1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivSkinCover1Cover: ImageView = view.findViewById(R.id.iv_skin_cover_1_cover)
    val tvSkinCover1Title: TextView = view.findViewById(R.id.tv_skin_cover_1_title)
    val ivSkinCover1Selected: ImageView = view.findViewById(R.id.iv_skin_cover_1_selected)
}

class ClassifyTab1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.text_view_1)
}

class RestoreFile1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ivRestoreFile1Icon: ImageView = view.findViewById(R.id.iv_restore_file_1_icon)
    val tvRestoreFile1Title: TextView = view.findViewById(R.id.tv_restore_file_1_title)
    val tvRestoreFile1Size: TextView = view.findViewById(R.id.tv_restore_file_1_size)
    val tvRestoreFile1LastModified: TextView =
        view.findViewById(R.id.tv_restore_file_1_last_modified)
}

class AnimeDownload1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAnimeDownload1Title: ImageView = view.findViewById(R.id.tv_anime_download_1_title)
    val tvAnimeDownload1Episode: TextView = view.findViewById(R.id.tv_anime_download_1_episode)
    val tvAnimeDownload1Size: TextView = view.findViewById(R.id.tv_anime_download_1_size)
    val pbAnimeDownload1: TextView = view.findViewById(R.id.pb_anime_download_1)
    val ivAnimeDownload1Cancel: TextView = view.findViewById(R.id.iv_anime_download_1_cancel)
}
