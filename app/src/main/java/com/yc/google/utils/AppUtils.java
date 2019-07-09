package com.yc.google.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.yc.google.model.bean.AppInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.GET_SIGNATURES;

public class AppUtils {
    public static List<AppInfo> getAppInfos(Context context, int filter) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));

        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        switch (filter) {
            case 0: // 所有应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    appInfos.add(getAppInfo(app, pm));
                }
                return appInfos;
            case 1: // 系统程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(getAppInfo(app, pm));
                    }
                }
                return appInfos;
            case 2: // 第三方应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    // 非系统程序
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfos.add(getAppInfo(app, pm));
                    }

                    // SDCard程序
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfos.add(getAppInfo(app, pm));
                    }
                }
                break;
            case 3:
                for (ApplicationInfo app : listAppcations) {
                    // 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                        appInfos.add(getAppInfo(app, pm));
                    }
                }
            default:
                return null;
        }
        return appInfos;
    }

    private static AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPackageName(app.packageName);
        appInfo.setApkUrl(app.sourceDir);
        File appFile = new File(app.sourceDir);
        long installed = appFile.lastModified();
        appInfo.setInstallTime(new SimpleDateFormat("yyyy年MM月dd日").format(new Date(installed)));
        appInfo.setSize(appFile.length());
        try {
            PackageInfo packageInfo = pm.getPackageInfo(app.packageName, GET_SIGNATURES);
            appInfo.setVersionName(packageInfo.versionName);
            Signature[] signs = packageInfo.signatures;
            appInfo.setSignature(CommonUtils.md5(signs[0].toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

}
