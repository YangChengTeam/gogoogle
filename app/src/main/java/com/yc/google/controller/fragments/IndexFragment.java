package com.yc.google.controller.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding3.view.RxView;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.utils.PathUtil;
import com.kk.utils.PreferenceUtil;
import com.kk.utils.ToastUtil;
import com.kk.utils.VUiKit;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.scottyab.rootbeer.RootBeer;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.constant.Config;
import com.yc.google.eventbus.GoogleSuiteInstallState;
import com.yc.google.eventbus.GoogleSuiteState;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.model.engin.GoogleSuiteEngin;
import com.yc.google.utils.CommonUtils;
import com.yc.google.view.widget.CheckStatusButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import rx.Subscriber;

import static android.content.pm.PackageManager.GET_SIGNATURES;

public class IndexFragment extends BaseFragment {

    @BindView(R.id.tv_toolbar_feedback)
    TextView mFeedbackBtn;

    @BindView(R.id.tv_check_brand_null)
    TextView mBrandNullTextView;

    @BindView(R.id.iv_check_brand)
    ImageView mBrankImageView;

    @BindView(R.id.tv_check_brand)
    TextView mBrandTextView;

    @BindView(R.id.tv_check_version)
    TextView mSysVersionTextView;

    @BindView(R.id.tv_check_root)
    TextView mRootTextView;

    @BindView(R.id.tv_main_go_tips)
    TextView mTipsTextView;

    @BindView(R.id.tv_main_check_status)
    TextView mCheckStatusTextView;

    @BindView(R.id.fab_check)
    FloatingActionButton mGoBtn;

    @BindViews({R.id.checkStatusButton_checkFragment_gsf, R.id.checkStatusButton_checkFragment_gsfLogin, R.id.checkStatusButton_checkFragment_gms, R.id.checkStatusButton_checkFragment_market})
    List<CheckStatusButton> mGoogleSuitStatusBtns;

    private List<AppInfo> appInfos;
    private List<String> unInstallApps;
    private int currentInstallIndex = 0;
    private CheckStatusButton currentCheckStatusButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_index;
    }

    @Override
    protected void initViews() {
        this.mBrandTextView.setText(TextUtils.concat(new CharSequence[]{"机型：", Build.MODEL}));
        this.mSysVersionTextView.setText(TextUtils.concat(new CharSequence[]{"Android ", Build.VERSION.RELEASE}));
        RootBeer rootBeer = new RootBeer(mContext);
        this.mRootTextView.setText(rootBeer.isRooted() ? R.string.label_brand_root : R.string.label_brand_root_no);
        this.mBrandNullTextView.setText(Build.BRAND);
        this.mBrandNullTextView.setVisibility(View.VISIBLE);

        RxView.clicks(mFeedbackBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            FeedbackAPI.openFeedbackActivity();
        });

        RxView.clicks(mGoBtn).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            go();
        });

        setupGoogleSuitStatus();
    }

    private void go() {
        currentInstallIndex = 0;
        mGoBtn.hide();
        mTipsTextView.setVisibility(View.GONE);
        mCheckStatusTextView.setVisibility(View.VISIBLE);
        mCheckStatusTextView.setText("努力安装中...");
        checkGoogleSuitStatus(false);
        getGoogleSuitInfo();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GoogleSuiteInstallState googleSuiteInstallState) {
        if (googleSuiteInstallState == null) return;

        if (googleSuiteInstallState == GoogleSuiteInstallState.GO) {
            go();
            return;
        } else if (googleSuiteInstallState == GoogleSuiteInstallState.UNINSTALLING || googleSuiteInstallState == GoogleSuiteInstallState.DOWNLOADING) {
            return;
        } else if (googleSuiteInstallState == GoogleSuiteInstallState.INSTALLING) {
            if (CommonUtils.isPackageInstalled(mContext, unInstallApps.get(currentInstallIndex))) {
                currentCheckStatusButton.setSuccessStatus();
                if (currentInstallIndex < unInstallApps.size() - 1) {
                    currentInstallIndex++;
                    installGoogleSuite();
                } else {
                    mCheckStatusTextView.setText(getString(R.string.label_all_install));
                    App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.STOP);
                    App.getApp().setGoogleSuiteState(GoogleSuiteState.COMPLETE);
                    EventBus.getDefault().post(App.getApp().getGoogleSuiteState());
                }
                return;
            }
        }
        mCanDownloadIn2345G = false;
        App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.STOP);
        checkGoogleSuitStatus(true);
    }

    private void getGoogleSuitInfo() {
        String[] packageNames = new String[]{"com.google.android.gsf", "com.google.android.gsf.login", "com.google.android.gms", "com.android.vending"};
        for (int i = 0; i < unInstallApps.size(); i++) {
            for (int j = 0; j < packageNames.length; j++) {
                if (packageNames[j].equals(unInstallApps.get(i))) {
                    mGoogleSuitStatusBtns.get(j).setProcessStatus();
                    break;
                }
            }
        }
        new GoogleSuiteEngin(mContext).getGoogleSuitInfo().subscribe(new Subscriber<ResultInfo<List<AppInfo>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                checkGoogleSuitStatus(true);
                ToastUtil.toast2(mContext, getString(R.string.label_network_wrong));
            }

            @Override
            public void onNext(ResultInfo<List<AppInfo>> listResultInfo) {
                if (listResultInfo != null && listResultInfo.getData() != null) {
                    appInfos = listResultInfo.getData();
                    installGoogleSuite();
                } else {
                    checkGoogleSuitStatus(true);
                    if (listResultInfo.getMsg() != null) {
                        ToastUtil.toast2(mContext, listResultInfo.getMsg());
                    }
                }
            }
        });
    }

    private AppInfo getAppInfo() {
        String packageName = unInstallApps.get(currentInstallIndex);
        AppInfo result = null;
        for (int j = 0; j < appInfos.size(); j++) {
            AppInfo appInfo = appInfos.get(j);
            if (appInfo != null && packageName.equals(appInfo.getPackageName())) {
                result = appInfo;
                break;
            }
        }
        return result;
    }

    private boolean mCanDownloadIn2345G = false;

    private void installGoogleSuite() {
        AppInfo appInfo = getAppInfo();
        currentCheckStatusButton = getCheckStatusButton(appInfo);
        if (appInfo == null || currentCheckStatusButton == null) return;

        String path = getGoogleSuiteDir();
        File apkFile = new File(path + "/" + appInfo.getAppName() + ".apk");

        if (apkFile.exists()) {
            boolean isApk = CommonUtils.isApk(mContext, apkFile.getAbsolutePath());
            if (isApk) {
                App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.INSTALLING);
                currentCheckStatusButton.setTextStatus("安装中");
                Uri apkUri = CommonUtils.getUriFromFile(mContext, apkFile);
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(installIntent);
                return;
            }
        }
        boolean canDownloadIn2345G = PreferenceUtil.getImpl(mContext).getBoolean(Config.SWITCH_4G, false);
        if (mCanDownloadIn2345G || CommonUtils.getNetworkType(mContext) != CommonUtils.NETTYPE_TYPE_MOBILE || canDownloadIn2345G) {
            App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.DOWNLOADING);
            downloadGoogleSuite(appInfo);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            builder.setCancelable(false);
            builder.setMessage("当前为非WIFI环境,继续下载会消耗流量，是否继续下载？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCanDownloadIn2345G = true;
                    App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.DOWNLOADING);
                    downloadGoogleSuite(appInfo);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.STOP);
                    EventBus.getDefault().post(App.getApp().getGoogleSuiteInstallState());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private CheckStatusButton getCheckStatusButton(AppInfo appInfo) {
        CheckStatusButton checkStatusButton = null;
        String[] packageNames = new String[]{"com.google.android.gsf", "com.google.android.gsf.login", "com.google.android.gms", "com.android.vending"};
        for (int j = 0; j < packageNames.length; j++) {
            if (packageNames[j].equals(appInfo.getPackageName())) {
                checkStatusButton = mGoogleSuitStatusBtns.get(j);
                break;
            }
        }
        return checkStatusButton;
    }

    private String getGoogleSuiteDir() {
        return PathUtil.createDir(mContext, "/googleSuit");
    }

    private void downloadGoogleSuite(AppInfo appInfo) {
        File dir = new File(getGoogleSuiteDir());
        DownloadTask task = new DownloadTask.Builder(appInfo.getApkUrl(), dir)
                .setFilename(appInfo.getAppName() + ".apk")
                .setMinIntervalMillisCallbackProcess(30)
                .setPassIfAlreadyCompleted(false)
                .build();
        task.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {

            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
                BreakpointInfo info = OkDownload.with().breakpointStore().get(task.getId());
                if (info != null) {
                    currentCheckStatusButton.setTextStatus((int) (info.getTotalOffset() / (float) info.getTotalLength() * 100) + "%");
                }
            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                installGoogleSuite();
            }
        });
    }

    private void setupGoogleSuitStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            ((View) mGoogleSuitStatusBtns.get(1).getParent()).setVisibility(View.GONE);
        } else {
            ((View) mGoogleSuitStatusBtns.get(1).getParent()).setVisibility(View.VISIBLE);
        }

        mGoBtn.hide();
        mTipsTextView.setVisibility(View.GONE);
        mCheckStatusTextView.setText("努力检测中...");

        VUiKit.postDelayed(2000, () -> {
            App.getApp().setGoogleSuiteInstallState(GoogleSuiteInstallState.STOP);
            checkGoogleSuitStatus(true);
        });
    }

    private void checkGoogleSuitStatus(boolean first) {
        if (GoogleSuiteInstallState.DOWNLOADING == App.getApp().getGoogleSuiteInstallState())
            return;

        String[] packageNames = new String[]{"com.google.android.gsf", "com.google.android.gsf.login", "com.google.android.gms", "com.android.vending"};
        int googleSuitInstalledCount = 0;
        unInstallApps = new ArrayList<>();
        for (int i = 0; i < packageNames.length; i++) {
            CheckStatusButton checkStatusButton = mGoogleSuitStatusBtns.get(i);
            if (((View) checkStatusButton.getParent()).getVisibility() == View.GONE) {
                googleSuitInstalledCount++;
                continue;
            }

            if (CommonUtils.isPackageInstalled(mContext, packageNames[i])) {
                googleSuitInstalledCount++;
                checkStatusButton.setSuccessStatus();
            } else {
                checkStatusButton.setFailStatus();
                unInstallApps.add(packageNames[i]);
            }
        }
        if (googleSuitInstalledCount == packageNames.length) {
            mGoBtn.hide();
            mTipsTextView.setVisibility(View.GONE);
            mCheckStatusTextView.setVisibility(View.VISIBLE);
            mCheckStatusTextView.setText(getString(R.string.label_all_install));
            App.getApp().setGoogleSuiteState(GoogleSuiteState.COMPLETE);
        } else {
            App.getApp().setGoogleSuiteState(GoogleSuiteState.INCOMPLETE);
            if (first) {
                mTipsTextView.setVisibility(View.VISIBLE);
                mCheckStatusTextView.setVisibility(View.INVISIBLE);
                mGoBtn.show();
            }
        }
        EventBus.getDefault().post(App.getApp().getGoogleSuiteState());
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
