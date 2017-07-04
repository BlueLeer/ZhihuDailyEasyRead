package com.leer.ZhihuDailyEasyRead.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Leer on 2017/6/30.
 */

public interface MyRetrofit {

    //获取最新的消息
    @GET(HttpConstant.LATEST)
    Call<ZhihuLatest> getZhihuLatest();


    //获取最新的消息
    @GET(HttpConstant.LATEST)
    Call<ResponseBody> getZhihuLatestJson();

    //获取以前的内容
    @GET(HttpConstant.BEFORE + "{date}")
    Call<ResponseBody> getZhihuBeforeJson(@Path("date") String date);

    //获取主题日报列表
    //https://news-at.zhihu.com/api/4/themes
    @GET(HttpConstant.THEME_LIST)
    Call<ResponseBody> getZhihuThemeListJson();

    //获取主题日报下面的文章列表
    @GET(HttpConstant.THEME + "{theme_id}")
    Call<ResponseBody> getZhihuThemeJson(@Path("theme_id") String theme_id);

    //根据文章的id获取具体的文章信息
    @GET(HttpConstant.ARTICLE + "{article_id}")
    Call<ResponseBody> getZhihuArticleJson(@Path("article_id") String article_id);
}
