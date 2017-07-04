package com.leer.ZhihuDailyEasyRead.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leer.ZhihuDailyEasyRead.R;
import com.leer.ZhihuDailyEasyRead.http.HttpConstant;
import com.leer.ZhihuDailyEasyRead.http.MyRetrofit;
import com.leer.ZhihuDailyEasyRead.http.ZhihuThemeList;
import com.leer.ZhihuDailyEasyRead.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Leer on 2017/7/2.
 */

public class ThemePickFragment extends DialogFragment {
    private static int currentTheme;
    private ListView mList_view;

    //更新数据
    private static final int MSG_SET_DATA = 101;

    private ArrayList<ZhihuThemeList.Theme> mThemes = new ArrayList<>();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_DATA:
                    //刷新数据
                    if (mAdapter == null) {
                        mAdapter = new MyAdapter();
                    }

                    mList_view.setAdapter(mAdapter);
                    break;
            }
        }
    };
    private LayoutInflater mLayoutInflater;
    private MyAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLayoutInflater = LayoutInflater.from(getContext());

        View view = mLayoutInflater.inflate(R.layout.fragment_themepick, null, false);
        mList_view = (ListView) view.findViewById(R.id.list_view_themes);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        //设置dialog进入退出时的动画
        alertDialog.getWindow().setWindowAnimations(R.style.DialogStyle);

        mList_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(null);
                    }
                    alertDialog.dismiss();
                } else {
                    //大公司日报,财经日报,设计日报,互联网安全,动漫日报暂时不支持手机查看暂时不支持
                    ZhihuThemeList.Theme theme = mThemes.get(position - 1);
                    if (theme.id.equals("5") || theme.id.equals("6") ||
                            theme.id.equals("4") || theme.id.equals("10") || theme.id.equals("9")
                            || theme.id.equals("11")) {
                        ToastUtil.show("不支持呦~");
                    } else {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(theme);
                        }
                        alertDialog.dismiss();
                    }
                }
            }
        });
        return alertDialog;
    }

    //返回当前选择的主题内容
    public static ThemePickFragment getInstance(int currentTheme) {
        currentTheme = currentTheme;

        ThemePickFragment themePickFragment = new ThemePickFragment();
        return themePickFragment;
    }

    //初始化所有的主题列表数据
    public void initData() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(HttpConstant.BASE_URL).build();
        MyRetrofit myRetrofit = retrofit.create(MyRetrofit.class);

        Call<ResponseBody> zhihuThemeListJson = myRetrofit.getZhihuThemeListJson();
        zhihuThemeListJson.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    parseJson(result);
                } catch (IOException e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void parseJson(String json) {
        Gson gson = new Gson();
        ZhihuThemeList zhihuThemeList = gson.fromJson(json, ZhihuThemeList.class);
        mThemes = zhihuThemeList.others;

        mHandler.sendEmptyMessage(MSG_SET_DATA);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mThemes.size() + 1;
        }

        @Override
        public ZhihuThemeList.Theme getItem(int position) {
            return mThemes.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_view_item_theme_pick, null, false);

                viewHolder = new ViewHolder();
                viewHolder.iv_theme_icon = (ImageView) convertView.findViewById(R.id.iv_theme_icon);
                viewHolder.tv_theme_name = (TextView) convertView.findViewById(R.id.tv_theme_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.tv_theme_name.setText("今日热闻");
                viewHolder.iv_theme_icon.setImageResource(R.drawable.latest_icon);
            } else {
                ZhihuThemeList.Theme theme = getItem(position);
                viewHolder.tv_theme_name.setText(theme.name);
                Picasso.with(getContext()).load(theme.thumbnail).into(viewHolder.iv_theme_icon);
            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_theme_name;
        ImageView iv_theme_icon;
    }

    public interface OnItemClickListener {
        void onItemClick(ZhihuThemeList.Theme theme);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
