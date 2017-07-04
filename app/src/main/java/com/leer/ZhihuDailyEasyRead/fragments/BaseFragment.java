package com.leer.ZhihuDailyEasyRead.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leer.ZhihuDailyEasyRead.activities.MainActivity;

/**
 * Created by Leer on 2017/6/30.
 */

public abstract class BaseFragment extends Fragment {
    protected boolean isDay;
    protected MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        isDay = mActivity.isDay();
        View view = initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public abstract View initView();

    public abstract void initData();
}
