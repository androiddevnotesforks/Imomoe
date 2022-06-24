# 数据源接口版本更新日志

## 202206242210

### 相较于202205211312版本的区别

1. **202205211312**版本的数据源**仍然可以使用**，但建议尽快升级
1. 增加**WebSource工具类**，可以直接**获取网页源码**（iframe的内容不会执行）
1. 新增**com.skyd.imomoe.adsapi**包，专为数据源提供工具类或方法



## 202205211312

### 相较于202204191624版本的区别

1. 移除IUtil接口内的getDetailLinkByEpisodeLink方法，现在详情页的partUrl直接存放于com.skyd.imomoe.bean.PlayBean的detailPartUrl变量中

1. com.skyd.imomoe.net.RetrofitManager类可以在数据源中使用了

1. com.skyd.imomoe.net.RetrofitManager$Companion类可以在数据源中使用了

1. retrofit.*可以在数据源中使用了

1. com.google.gson.**可以在数据源中使用了
