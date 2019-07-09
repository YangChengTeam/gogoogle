package com.yc.google.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.kk.utils.ToastUtil;
import com.kk.utils.VUiKit;
import com.yc.google.R;
import com.yc.google.constant.Config;
import com.yc.google.model.bean.AppInfo;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;

import static android.content.pm.PackageManager.GET_SIGNATURES;

public class CommonUtils {
    public static boolean isPackageInstalled(Context context, String packageName) {
        boolean found = true;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriFromFileForN(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    private static Uri getUriFromFileForN(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".GoogleSuitFileProvider", file);
        return fileUri;
    }

    public static String getHrSize(long size) {
        String hrSize = "";
        double m = size / 1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1024.0) {
            m = m / 1024.0;
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(m).concat(" KB");
        }
        return hrSize;
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络   1：WIFI网络   2：2G/3G/4G
     */
    public static final int NETTYPE_NO = 0x00;
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_TYPE_MOBILE = 0x02;

    public static int getNetworkType(Context context) {
        int netType = NETTYPE_NO;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (extraInfo != null && !extraInfo.isEmpty()) {
                netType = NETTYPE_TYPE_MOBILE;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


    public static String md5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString().toUpperCase();
    }

    public static void openApp(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
        if (apps.size() > 0) {
            ResolveInfo ri = apps.get(0);
            String className = ri.activityInfo.name;
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void uninstallApp(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);

    }

    public static void openPremissionSetting(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    public static boolean isApk(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        boolean isApk = true;
        try {
            PackageInfo info = pm.getPackageArchiveInfo(path, GET_SIGNATURES);
            if (info == null) {
                isApk = false;
            }
        } catch (Exception e) {
            isApk = false;
        }
        return isApk;
    }

    public static void viewAppDetailInfo(Context context, AppInfo appInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(R.layout.dialog_app_manager_more, null);

        ((TextView) inflate.findViewById(R.id.tv_dialog_app_manage_name)).setText(appInfo.getAppName());
        ((TextView) inflate.findViewById(R.id.tv_dialog_app_manage_package)).setText(appInfo.getPackageName());
        ((TextView) inflate.findViewById(R.id.tv_dialog_app_manage_version)).setText(TextUtils.concat(new CharSequence[]{"V ", appInfo.getVersionName()}));
        ((TextView) inflate.findViewById(R.id.tv_dialog_app_manage_md5)).setText(appInfo.getSignature());
        ((TextView) inflate.findViewById(R.id.tv_dialog_app_manage_file_path)).setText(appInfo.getApkUrl());


        builder.setPositiveButton("复制", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CharSequence concat = TextUtils.concat(new CharSequence[]{"名称：", appInfo.getAppName(), "\n", "包名：", appInfo.getPackageName() + "\n版本：", "V ", appInfo.getVersionName() + "\nMD5：", appInfo.getSignature() + "\n位置：", appInfo.getApkUrl()});
                ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("appInfo", concat));
                ToastUtil.toast2(context, context.getString(R.string.success_copy));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void share2WX(Context context) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("shareInfo", Config.SHARE_URL));
        ToastUtil.toast2(context, context.getString(R.string.share_url_copy));
        ToastUtil.toast2(context, context.getString(R.string.share_url_copy));
        VUiKit.postDelayed(2000, () -> {
            CommonUtils.openApp(context, "com.tencent.mm");
        });
    }
}
