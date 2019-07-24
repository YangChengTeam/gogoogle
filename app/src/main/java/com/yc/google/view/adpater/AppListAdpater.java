package com.yc.google.view.adpater;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding3.view.RxView;
import com.kk.utils.ScreenUtil;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.model.bean.AppInfo;
import com.yc.google.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppListAdpater extends BaseQuickAdapter<AppInfo, BaseViewHolder> {

    public AppListAdpater(@Nullable List<AppInfo> data) {
        super(R.layout.list_item_local_app_result, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AppInfo item) {
        helper.setText(R.id.tv_local_app_name, item.getAppName());
        helper.setText(R.id.tv_local_app_install_time, item.getInstallTime());
        helper.setText(R.id.tv_local_app_size, CommonUtils.getHrSize(item.getSize()));
        helper.setText(R.id.tv_local_app_version_V, "V " + item.getVersionName());
        Glide.with(mContext).load(item.getAppIcon()).into((ImageView) helper.getView(R.id.iv_local_icon));
        View more = helper.getView(R.id.iv_local_app_more);
        more.setTag(item);
        RxView.clicks(more).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(view -> {
            showPopupWindow(more);
        });
    }

    private void showPopupWindow(View more) {
        ListPopupWindow menuPopupWindow = new ListPopupWindow(mContext);
        List<Map<String, String>> menuInfos = new ArrayList<>();
        Map<String, String> menuMap = new HashMap();
        menuMap.put("title", "打开");
        menuInfos.add(menuMap);

        menuMap = new HashMap();
        menuMap.put("title", "卸载");
        menuInfos.add(menuMap);

        menuMap = new HashMap();
        menuMap.put("title", "应用权限管理");
        menuInfos.add(menuMap);

        menuMap = new HashMap();
        menuMap.put("title", "更多应用信息");
        menuInfos.add(menuMap);

        menuPopupWindow.setAdapter(new SimpleAdapter(mContext, menuInfos, R.layout.list_item_popup_menu, new String[]{"title"}, new int[]{R.id.tv_popup_menu_title}));
        menuPopupWindow.setWidth(ScreenUtil.getWidth(mContext) / 2);
        menuPopupWindow.setHeight(-2);
        menuPopupWindow.setBackgroundDrawable(new ColorDrawable(-1));
        menuPopupWindow.setAnchorView(more);
        menuPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        menuPopupWindow.setModal(true);
        menuPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuPopupWindow.dismiss();
                AppInfo appInfo = (AppInfo) more.getTag();
                switch (position) {
                    case 0:
                        CommonUtils.openApp(mContext, appInfo.getPackageName());
                        break;
                    case 1:
                        App.getApp().setUninstallAppInfo(appInfo);
                        CommonUtils.uninstallApp(mContext, appInfo.getPackageName());
                        break;
                    case 2:
                        CommonUtils.openPremissionSetting(mContext, appInfo.getPackageName());
                        break;
                    case 3:
                        CommonUtils.viewAppDetailInfo(mContext, appInfo);
                        break;
                }
            }
        });
        menuPopupWindow.show();
    }


}
