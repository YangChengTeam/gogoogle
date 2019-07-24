package com.yc.google.view.adpater;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding3.view.RxView;
import com.kk.utils.LogUtil;
import com.kk.utils.ToastUtil;
import com.kk.utils.VUiKit;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.eventbus.GoogleSuiteInstallState;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.model.bean.StatusInfo;
import com.yc.google.utils.AppUtils;
import com.yc.google.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GoogleSuiteStatusAdpater extends BaseMultiItemQuickAdapter<StatusInfo, BaseViewHolder> {

    private AppListAdpater appListAdpater;
    private RecyclerView recyclerView;

    private int page = 1;
    private int pagecount = 10;
    private List<AppInfo> currentInfos = new ArrayList();

    public GoogleSuiteStatusAdpater(List<StatusInfo> data) {
        super(data);
        EventBus.getDefault().register(this);

        addItemType(0, R.layout.list_item_repair_result_head);
        addItemType(1, R.layout.list_item_repair_local_app);

        loadData();
        appListAdpater = new AppListAdpater(currentInfos);

    }

    private boolean loadData() {
        List<AppInfo> appInfos = App.getApp().getAppInfos();
        int count = 0;
        if (appInfos != null) {
            for (int i = (page - 1) * pagecount; i < page * pagecount && i < appInfos.size(); i++) {
                currentInfos.add(appInfos.get(i));
                count++;
            }
            page++;
        }
        return count == pagecount;
    }

    @Override
    protected void convert(BaseViewHolder helper, StatusInfo item) {
        int type = helper.getItemViewType();
        switch (type) {
            case 0:
                helper.setText(R.id.tv_repaired_title, item.getTitle());
                if (item.getDesp() != null && item.getDesp().indexOf("点击此处") != -1) {
                    CharSequence spannableString = new SpannableString(item.getDesp());
                    ((SpannableString) spannableString).setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            CommonUtils.share2WX(mContext);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(true);
                        }
                    }, item.getDesp().indexOf("点击此处"), item.getDesp().indexOf("点击此处") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    TextView textView = helper.getView(R.id.tv_repaired_info);
                    textView.setText(spannableString);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    helper.setText(R.id.tv_repaired_info, item.getDesp());
                }
                helper.setText(R.id.tv_repaired_operate, item.getAction());
                if (item.getAction() == mContext.getString(R.string.repaired_operate_onekey)) {
                    helper.setVisible(R.id.tv_repaired_operate, true);
                    RxView.clicks(helper.getView(R.id.tv_repaired_operate)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
                        App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.GO);
                        EventBus.getDefault().post(App.getApp().getGoogleSuiteInstallState());

                    });
                } else {
                    helper.setVisible(R.id.tv_repaired_operate, false);
                    RxView.clicks(helper.getView(R.id.tv_repaired_operate)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            ToastUtil.toast2(mContext, "您没有安装应用市场App");
                            e.printStackTrace();
                        }
                    });
                }
                break;
            case 1:
                if (recyclerView != null) return;

                ((View) helper.getView(R.id.tv_repair_local_app_title).getParent().getParent()).setVisibility(View.GONE);
                recyclerView = helper.getView(R.id.recycler_repair_local_app);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(appListAdpater);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

                VUiKit.postDelayed(1000, this::showMore);

                ((View) helper.getView(R.id.tv_repair_local_app_title).getParent().getParent()).setVisibility(View.VISIBLE);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppInfo appInfo) {
        if (appInfo == null) return;
        currentInfos.remove(appInfo);
        appListAdpater.notifyDataSetChanged();
        App.getApp().setUninstallAppInfo(null);
    }

    private void showMore() {
        Observable.just("").map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return loadData();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    VUiKit.postDelayed(800, GoogleSuiteStatusAdpater.this::showMore);
                    appListAdpater.notifyDataSetChanged();
                }
            }
        });
    }

    private void getAppList(BaseViewHolder helper) {
        if (App.getApp().getAppInfos() != null) return;

        Observable.just(2).map(new Func1<Integer, List<AppInfo>>() {
            @Override
            public List<AppInfo> call(Integer integer) {
                List<AppInfo> appInfos = AppUtils.getAppInfos(mContext, 2);
                return appInfos;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<AppInfo>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<AppInfo> appInfos) {
                ((View) helper.getView(R.id.tv_repair_local_app_title).getParent().getParent()).setVisibility(View.VISIBLE);
                App.getApp().setAppInfos(appInfos);
            }
        });
    }

}
