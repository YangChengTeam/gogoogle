package com.yc.google.controller.fragments;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.jakewharton.rxbinding3.view.RxView;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.eventbus.GoogleSuiteState;
import com.yc.google.model.bean.StatusInfo;
import com.yc.google.view.adpater.GoogleSuiteStatusAdpater;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class AppListFragment extends BaseFragment {

    @BindView(R.id.tv_toolbar_feedback)
    TextView mFeedbackBtn;

    @BindView(R.id.rv_repair_root)
    RecyclerView mGoogleSuiteStatusRecyclerView;

    private GoogleSuiteStatusAdpater mGoogleSuiteStatusAdpater;
    private StatusInfo mStatusInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_app_list;
    }

    private void setSuiteStateInfo(GoogleSuiteState googleSuiteState) {
        if (googleSuiteState == null) {
            googleSuiteState = App.getApp().getGoogleSuiteState();
        }
        if (googleSuiteState == null || mStatusInfo == null) return;

        if (googleSuiteState == GoogleSuiteState.COMPLETE) {
            mStatusInfo.setTitle(getString(R.string.label_all_install));
            String desp = "您已经成功安装了谷歌套件，点击此处分享给更多的谷歌爱好者！";
            mStatusInfo.setDesp(desp);
            mStatusInfo.setAction(getString(R.string.repaired_operate_repair_rate));
        } else {
            mStatusInfo.setTitle(getString(R.string.label_wait_repair_title));
            mStatusInfo.setDesp(getString(R.string.label_wait_repair_info));
            mStatusInfo.setAction(getString(R.string.repaired_operate_onekey));
        }
    }

    @Override
    protected void initViews() {
        List<StatusInfo> statusInfos = new ArrayList<>();
        if (mStatusInfo == null) {
            mStatusInfo = new StatusInfo();
        }
        mStatusInfo.setItemType(0);
        mStatusInfo.setTitle("检测中");
        mStatusInfo.setDesp("请稍后...");
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setItemType(1);
        statusInfos.add(mStatusInfo);
        statusInfos.add(statusInfo);
        setSuiteStateInfo(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mGoogleSuiteStatusRecyclerView.setLayoutManager(linearLayoutManager);
        mGoogleSuiteStatusAdpater = new GoogleSuiteStatusAdpater(statusInfos);
        mGoogleSuiteStatusRecyclerView.setAdapter(mGoogleSuiteStatusAdpater);

        RxView.clicks(mFeedbackBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            FeedbackAPI.openFeedbackActivity();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GoogleSuiteState googleSuiteState) {
        if (googleSuiteState == null) return;
        setSuiteStateInfo(googleSuiteState);
        mGoogleSuiteStatusAdpater.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
