package com.yc.google.model.engin;

import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.google.App;
import com.yc.google.constant.Config;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.model.bean.PhoneInfo;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

public class GoogleSuiteEngin extends BaseEngin {

    public GoogleSuiteEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.GET_APP_INFO_URL;
    }

    public Observable<ResultInfo<List<AppInfo>>> getGoogleSuitInfo() {
        PhoneInfo phoneInfo = App.getApp().getPhoneInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("versionCode", "407");
        params.put("channel", "CoolM");
        params.put("param", "{\"apiVer\":1,\"guid\":\""+phoneInfo.getGuid()+"\",\"sdkVersion\":"+ Build.VERSION.SDK_INT +",\"cup64\":"+phoneInfo.isCpu64()+",\"brand\":\""+phoneInfo.getBrand()+"\",\"isRoot\":"+phoneInfo.isRoot()+",\"x86\":"+phoneInfo.isX86()+",\"dpi\":"+phoneInfo.getDip()+",\""+phoneInfo.getRom()+"\":\"EMUI\",\"androidVersion\":\""+phoneInfo.getAndroidVersion()+"\",\"packages\":"+ phoneInfo.getPackages() +"}");
        return rxpost(new TypeReference<ResultInfo<List<AppInfo>>>() {
        }.getType(), params, false, false, false);
    }
}
