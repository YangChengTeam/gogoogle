package com.yc.google.controller.activitys;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.kk.utils.TaskUtil;
import com.kk.utils.ToastUtil;
import com.kk.utils.VUiKit;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.utils.AppUtils;
import com.yc.google.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.app_logo)
    ImageView mAppLogoImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermission()) {
                getAppList();
            }
        } else {
            getAppList();
        }
    }

    private void getAppList() {
        Observable.just(2).map(new Func1<Integer, List<AppInfo>>() {
            @Override
            public List<AppInfo> call(Integer filter) {
                List<AppInfo> appInfos = AppUtils.getAppInfos(SplashActivity.this, filter);
                return appInfos;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<AppInfo>>() {
            @Override
            public void onCompleted() {
                nav2MainActivity();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<AppInfo> appInfos) {
                App.getApp().setAppInfos(appInfos);
            }
        });
    }

    private void nav2MainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();

        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            return true;
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
            return false;
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (hasAllPermissionsGranted(grantResults)) {
            getAppList();
        } else {
            ToastUtil.toast(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。");
            CommonUtils.openPremissionSetting(this, getPackageName());
            finish();
        }
    }
}
