# 数据源接口版本更新日志

## 202205211312

### 相较于202204191624版本的区别

1. 移除IUtil接口内的getDetailLinkByEpisodeLink方法，现在详情页的partUrl直接存放于com.skyd.imomoe.bean.PlayBean的detailPartUrl变量中

1. com.skyd.imomoe.net.RetrofitManager类可以在数据源中使用了

1. com.skyd.imomoe.net.RetrofitManager$Companion类可以在数据源中使用了

1. retrofit.*可以在数据源中使用了

1. com.google.gson.**可以在数据源中使用了
