package com.leer.ZhihuDailyEasyRead.http;

import java.util.ArrayList;

/**
 * Created by Leer on 2017/7/2.
 */

public class Article {
    public String id; //文章的ID
    public String title; // 文章的标题
    public String body; // 文章的内容
    public String image; //大图
    public ArrayList<String> js; // 供手机端的 WebView(UIWebView) 使用
    public ArrayList<String> css; // 供手机端的 WebView(UIWebView) 使用
}
