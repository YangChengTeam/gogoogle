package com.yc.google.controller.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.jakewharton.rxbinding3.view.RxView;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.utils.ToastUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.controller.activitys.AboutActivity;
import com.yc.google.controller.activitys.SettingActivity;
import com.yc.google.eventbus.GoogleSuiteInstallState;
import com.yc.google.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class MyFragment extends BaseFragment {
    @BindView(R.id.rl_manage_uninstall)
    RelativeLayout mUninstallBtn;

    @BindView(R.id.rl_manage_about)
    RelativeLayout mAboutBtn;

    @BindView(R.id.rl_manage_update)
    RelativeLayout mUpdateBtn;

    @BindView(R.id.tvVersion)
    TextView mVersionTextView;

    @BindView(R.id.rl_manage_feedback)
    RelativeLayout mFeedbackBtn;

    @BindView(R.id.rl_manage_setting)
    RelativeLayout mSettingBtn;

    @BindView(R.id.rl_manage_share)
    RelativeLayout mShareBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initViews() {
        RxView.clicks(mUninstallBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            showUninstallDialog();
        });

        RxView.clicks(mAboutBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(mContext, AboutActivity.class);
            startActivity(intent);
        });

        RxView.clicks(mUpdateBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            Beta.checkUpgrade(true, false);
        });

        RxView.clicks(mFeedbackBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            FeedbackAPI.openFeedbackActivity();
        });

        RxView.clicks(mSettingBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            Intent intent = new Intent(mContext, SettingActivity.class);
            startActivity(intent);
        });

        RxView.clicks(mShareBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            CommonUtils.share2WX(mContext);
        });

        mVersionTextView.setText(TextUtils.concat(new CharSequence[]{"V", GoagalInfo.get().packageInfo.versionName}));
    }

    private String getNameByPackage(String packageName) {
        String name = "";
        if ("com.google.android.gsf".equals(packageName)) {
            name = getString(R.string.google_framework);
        } else if ("com.google.android.gsf.login".equals(packageName)) {
            name = getString(R.string.google_login_service);
        } else if ("com.google.android.gms".equals(packageName)) {
            name = getString(R.string.google_play_service);
        } else if ("com.android.vending".equals(packageName)) {
            name = getString(R.string.google_play);
        }
        return name;
    }

    private String getPackageByName(String name) {
        String packageName = "";
        if (getString(R.string.google_framework).equals(name)) {
            packageName = "com.google.android.gsf";
        } else if (getString(R.string.google_login_service).equals(name)) {
            packageName = "com.google.android.gsf.login";
        } else if (getString(R.string.google_play_service).equals(name)) {
            packageName = "com.google.android.gms";
        } else if (getString(R.string.google_play).equals(name)) {
            packageName = "com.android.vending";
        }
        return packageName;
    }

    private int currentUnInstallIndex = 0;
    private List<String> currentUnInstallAppInfos = null;
    private int unInstallCount = 0;

    private void showUninstallDialog() {
        currentUnInstallAppInfos = new ArrayList();
        String[] packageNames = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            packageNames = new String[]{"com.google.android.gsf", "com.google.android.gms", "com.android.vending"};
        } else {
            packageNames = new String[]{"com.google.android.gsf", "com.google.android.gsf.login", "com.google.android.gms", "com.android.vending"};
        }
        for (int i = 0; i < packageNames.length; i++) {
            if (CommonUtils.isPackageInstalled(mContext, packageNames[i])) {
                currentUnInstallAppInfos.add(getNameByPackage(packageNames[i]));
            }
        }
        unInstallCount = currentUnInstallAppInfos.size();
        CharSequence[] items = new CharSequence[currentUnInstallAppInfos.size()];
        for (int i = 0; i < currentUnInstallAppInfos.size(); i++) {
            items[i] = currentUnInstallAppInfos.get(i);
        }

        if (items.length == 0) {
            ToastUtil.toast2(mContext, getString(R.string.success_uninstall_common));
            return;
        }

        boolean[] checkedItems = new boolean[items.length];
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = true;
        }

        AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle)
                .setTitle("卸载后谷歌服务将不能正常使用，请谨慎操作")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            currentUnInstallAppInfos.add(items[indexSelected].toString());
                        } else {
                            for (String name : currentUnInstallAppInfos) {
                                if (name.equals(items[indexSelected])) {
                                    currentUnInstallAppInfos.remove(name);
                                    break;
                                }
                            }
                        }
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        currentUnInstallIndex = 0;
                        uninstallAppInfo();
                    }
                }).setNegativeButton("取消", null).create();
        dialog.show();
    }

    private void uninstallAppInfo() {
        String packageName = getPackageByName(currentUnInstallAppInfos.get(currentUnInstallIndex));
        App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.UNINSTALLING);
        CommonUtils.uninstallApp(mContext, packageName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GoogleSuiteInstallState googleSuiteInstallState) {
        if (googleSuiteInstallState == null) return;
        if (googleSuiteInstallState == GoogleSuiteInstallState.UNINSTALLING) {
            String packageName = getPackageByName(currentUnInstallAppInfos.get(currentUnInstallIndex));
            if (!CommonUtils.isPackageInstalled(mContext, packageName)) {
                currentUnInstallIndex++;
                if (currentUnInstallIndex < currentUnInstallAppInfos.size()) {
                    uninstallAppInfo();
                    return;
                } else if (unInstallCount == currentUnInstallIndex) {
                    ToastUtil.toast2(mContext, getString(R.string.success_uninstall_common));
                }
            }
            App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.STOP);
            EventBus.getDefault().post(App.getApp().getGoogleSuiteInstallState());
        }
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
