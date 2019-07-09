package com.yc.google;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.kk.utils.FileUtil;
import com.kk.utils.LogUtil;
import com.kk.utils.TaskUtil;
import com.scottyab.rootbeer.RootBeer;
import com.tencent.bugly.Bugly;
import com.umeng.commonsdk.UMConfigure;
import com.yc.google.eventbus.GoogleSuiteInstallState;
import com.yc.google.eventbus.GoogleSuiteState;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.model.bean.ChannelInfo;
import com.yc.google.model.bean.PhoneInfo;
import com.yc.google.utils.RomUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class App extends Application {
    private ChannelInfo channelInfo;

    private PhoneInfo phoneInfo;

    public PhoneInfo getPhoneInfo() {
        return phoneInfo;
    }

    public void initPhoneInfo() {
        phoneInfo = new PhoneInfo();
        phoneInfo.setBrand(Build.BRAND);
        phoneInfo.setAndroidVersion(Build.VERSION.RELEASE);
        RootBeer rootBeer = new RootBeer(this);
        phoneInfo.setRoot(rootBeer.isRooted());
        phoneInfo.setCpu64(true);
        phoneInfo.setGuid(GoagalInfo.get().uuid);
        phoneInfo.setRom(RomUtils.getRomInfo());
        phoneInfo.setPackages("[\"com.google.android.gsf\",\"com.google.android.gsf.login\",\"com.google.android.gms\",\"com.android.vending\"]");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
        phoneInfo.setDip(densityDpi);
        String arch = System.getProperty("os.arch");
        phoneInfo.setX86(arch.contains("x86") ? true : false);
    }

    private GoogleSuiteState googleSuiteState;

    public GoogleSuiteState getGoogleSuiteState() {
        return googleSuiteState;
    }

    public void setGoogleSuiteState(GoogleSuiteState googleSuiteState) {
        this.googleSuiteState = googleSuiteState;
    }

    private GoogleSuiteInstallState googleSuiteInstallState;

    public GoogleSuiteInstallState getGoogleSuiteInstallState() {
        return googleSuiteInstallState;
    }

    public void setGoogleSuiteInstallState(GoogleSuiteInstallState googleSuiteInstallState) {
        this.googleSuiteInstallState = googleSuiteInstallState;
    }

    private List<AppInfo> appInfos;

    public List<AppInfo> getAppInfos() {
        return appInfos;
    }

    public void setAppInfos(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    private static App app;

    public static App getApp() {
        return app;
    }

    private AppInfo uninstallAppInfo;

    public AppInfo getUninstallAppInfo() {
        return uninstallAppInfo;
    }

    public void setUninstallAppInfo(AppInfo uninstallAppInfo) {
        this.uninstallAppInfo = uninstallAppInfo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        inits();
    }

    private void inits() {
        TaskUtil.getImpl().runTask(() -> {
            initPhoneInfo();
            initHttpInfo();
            FeedbackAPI.init(this, "27676868", "eb8940bd1ec89014b4ebb7a5fd9a0cc7 ");
            FeedbackAPI.setTranslucent(true);
            FeedbackAPI.setBackIcon(R.drawable.ic_arrow_back_white_24dp);
            FeedbackAPI.setHistoryTextSize(16.0f);
            Bugly.init(getApplicationContext(), "ff561805e1", false);
        });
    }

    private void initHttpInfo() {
        ApplicationInfo appinfo = getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zf = null;
        try {
            zf = new ZipFile(sourceDir);
            ZipEntry ze = zf.getEntry("META-INF/channelconfig.json");
            InputStream in = zf.getInputStream(ze);
            String result = FileUtil.readString(in);
            channelInfo = JSON.parseObject(result, ChannelInfo.class);
            LogUtil.msg("渠道来源->" + result);
        } catch (Exception e) {
            LogUtil.msg("apk中channelconfig.json文件不存在", LogUtil.W);
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
        }
        GoagalInfo.get().init(getApplicationContext());
        HttpConfig.setPublickey("-----BEGIN PUBLIC KEY-----\n" +
                "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAq1wNiX3iQt+Q7juXZDNR\n" +
                "Eq2jGqx+2pXM4ddoZ1rkHb3XJFhrBguI/R11IfmTioPlTnheJqYkJf0NGzzxW2t1\n" +
                "nDKwbjoZ+d7UMehCDV44+FQMtzhRAFjmcQIXn6AaL2bkJFzHvoTtYPqgqgT5V4L6\n" +
                "+DhLSuPSwIVAC1aw1+iUk3jbg3ETzERSS6LDHTRi2ng7rpKAeHKeJ2RtbrcetCxv\n" +
                "YF+6QabnJhZGtr6cvp9CtFv5bSc2JsCqbJbsDGM6OPAjQjtpmImxQiXcI1gko8WP\n" +
                "+k1nx9GPJBhtdAXORRGRoHA8fUCveAJPDw1jSF3lBDf+1BHx+XeVX4/sVybd5Rn3\n" +
                "IE21UeuF+kbmwULJKUDzQNIwlXA+k4faRhdKeFCOeqldozwhP+575L/vVlyvxx/M\n" +
                "UJdA4vUziyO1l/IQEGzJ7b4AWfJ6sQEKDjODuLM+DM9MAuYddFnNfKj8XVi3jx9y\n" +
                "0OOAb/4Rb3UPeOUF9R4Sr0nLmL/1ITL8/9rJaue/e/D7H4xfQNbCtSTPhsa/+UOt\n" +
                "j3AQsNUjqkoLMXm7vtXEIshXEm4mlmMl98LsXyK3B6lMiV7jO4Vyp8muga8I/nH3\n" +
                "Snw5e86AHSZdnbQcLTDx9sgqN2mSL3MqLp9oiL4KGxNdNdt8EunGRycTsj09o7oz\n" +
                "Lfxf+/8xTiWygyUTThX+/GUCAwEAAQ==\n" +
                "-----END PUBLIC KEY-----");
        //设置http默认参数
        String agent_id = "1";
        Map<String, String> params = new HashMap<>();
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }
        params.put("agent_id", agent_id);
        params.put("ts", System.currentTimeMillis() + "");
        params.put("device_type", "2");
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = android.os.Build.MODEL.contains(android.os.Build.BRAND) ? android.os.Build.MODEL + " " + android
                .os.Build.VERSION.RELEASE : Build.BRAND + " " + android
                .os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
        params.put("sv", sv);
        if (channelInfo != null) {
            params.put("site_id", channelInfo.getSite_id() + "");
            params.put("soft_id", channelInfo.getSoft_id() + "");
            params.put("referer_url", channelInfo.getNode_url() + "");
        }
        if (GoagalInfo.get().packageInfo != null) {
            params.put("app_version", GoagalInfo.get().packageInfo.versionCode + "");
        }
        HttpConfig.setDefaultParams(params);

        // 友盟统计
        UMConfigure.init(this, "5d2315c63fc195e1b1001167", "WebStore" + agent_id, UMConfigure.DEVICE_TYPE_PHONE, null);
    }

}
