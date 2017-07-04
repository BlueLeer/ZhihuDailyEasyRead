package com.leer.ZhihuDailyEasyRead.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.http.Article;
import com.leer.ZhihuDailyEasyRead.http.HttpConstant;
import com.leer.ZhihuDailyEasyRead.http.MyRetrofit;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Leer on 2017/7/2.
 */

public class ArticleActivity extends BaseActivity {
    private static final String ARG_ARTICLE_ID = "arg_article_id";
    private static final String IS_LIGHT = "is_light";
    //文章的ID
    private String mArticleId;

    private static final int SET_DATA = 101;

    private boolean isDay;

    private Article mArticle;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_DATA:
                    //数据加载成功,更新页面
                    setData();
                    break;
            }
        }
    };
    private CoordinatorLayout coordinatorLayout;
    private Toolbar mToolbar;
    private WebView mWebView;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acticity_article);

        //设置透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mArticleId = getIntent().getStringExtra(ARG_ARTICLE_ID);
        isDay = getIntent().getBooleanExtra(IS_LIGHT, true);

        initView();

        initData();
    }

    private void initView() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mImageView = (ImageView) findViewById(R.id.iv);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);

        updateTheme();
    }

    public void initData() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpConstant.BASE_URL).build();
        MyRetrofit myRetrofit = retrofit.create(MyRetrofit.class);
        Call<ResponseBody> zhihuArticleJson = myRetrofit.getZhihuArticleJson(mArticleId);
        zhihuArticleJson.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    parseJson(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private void parseJson(String json) {
        Gson gson = new Gson();
        mArticle = gson.fromJson(json, Article.class);

        mHandler.sendEmptyMessage(SET_DATA);
    }

    private void setData() {
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/css/news.css\" type=\"text/css\">";
        String c = "<link rel=\"stylesheet\" href=\"" +
                mArticle.css.get(0) +
                " type=\"text/css\">";
        String html = "<html><head>" + c + "</head><body>" + mArticle.body + "</body></html>";
        html = html.replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("x-data://base", html, "text/html", "UTF-8", null);

        Picasso.with(this).load(mArticle.image).into(mImageView);
        mToolbar.setTitle(mArticle.title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public static Intent newIntent(Context context, String article_id, boolean isLight) {
        Intent i = new Intent(context, ArticleActivity.class);

        i.putExtra(ARG_ARTICLE_ID, article_id);
        i.putExtra(IS_LIGHT, isLight);
        return i;
    }

    @Override
    protected void updateTheme() {
//        super.updateTheme();
//        mToolbar.setBackgroundColor(isDay ? mDayBackGroundTitleColor : mNightBackgroundColor);
//        mToolbar.setTitleTextColor(isDay ? mDayTextColor : mNightTextColor);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
