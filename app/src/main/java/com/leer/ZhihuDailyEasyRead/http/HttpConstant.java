package com.leer.ZhihuDailyEasyRead.http;

/**
 * Created by Leer on 2017/6/30.
 */

public class HttpConstant {
    //知乎日报的接口api
    public static final String BASE_URL = "https://news-at.zhihu.com/api/4/";

    //获取最新消息,拼接到BASE_URL末尾
    public static final String LATEST = "news/latest";

    //获取以前的日报内容,拼接到BASE_URL的末尾,并且带上/日期
    //例如:https://news-at.zhihu.com/api/4/news/before/20170630
    public static final String BEFORE = "news/before/";

    //获取主题日报的列表,拼接到BASE_URL后面
    public static final String THEME_LIST = "themes";

    //获取主题日报下面的文章列表
    //https://news-at.zhihu.com/api/4/theme/11
    public static final String THEME = "theme/";

    //加载文章内容,例如:
    //https://news-at.zhihu.com/api/4/news/8457237
    public static final String ARTICLE = "news/";
}
