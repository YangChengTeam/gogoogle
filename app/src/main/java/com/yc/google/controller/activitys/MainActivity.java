package com.yc.google.controller.activitys;


import android.view.KeyEvent;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.kk.utils.ToastUtil;
import com.yc.google.App;
import com.yc.google.R;
import com.yc.google.controller.fragments.AppListFragment;
import com.yc.google.controller.fragments.BaseFragment;
import com.yc.google.controller.fragments.IndexFragment;
import com.yc.google.controller.fragments.MyFragment;
import com.yc.google.eventbus.GoogleSuiteInstallState;
import com.yc.google.model.bean.AppInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.OnlyIconMaterialItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tab_main_bottom)
    PageNavigationView mTabLayout;

    private List<BaseFragment> mFragmentList;
    private NavigationController mNavController;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        initFragmentList();
        initTabLayout();
    }

    private void initFragmentList() {
        mFragmentList = new ArrayList<>();
        BaseFragment indexFragment = new IndexFragment();
        BaseFragment appListFragment = new AppListFragment();
        BaseFragment settingFragment = new MyFragment();
        mFragmentList.add(indexFragment);
        mFragmentList.add(appListFragment);
        mFragmentList.add(settingFragment);
    }


    private void initTabLayout() {
        mNavController = mTabLayout.custom()
                .addItem(newItem(R.drawable.ic_menu_first, R.drawable.ic_menu_first_check))
                .addItem(newItem(R.drawable.ic_menu_second, R.drawable.ic_menu_second_check))
                .addItem(newItem(R.drawable.ic_menu_third, R.drawable.ic_menu_third_check))
                .build();
        mNavController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                select(index, old);
            }

            @Override
            public void onRepeat(int index) {

            }
        });

        mNavController.setSelect(0);
        select(0, -1);

    }

    // 预加载
    private void preloadAppList(){
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.add(R.id.fl_main_content, mFragmentList.get(1)).commit();
        trx.hide(mFragmentList.get(1));
    }

    private void select(int index, int old) {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        if (old >= 0 && old < mFragmentList.size()) {
            trx.hide(mFragmentList.get(old));
        }
        if (!mFragmentList.get(index).isAdded()) {
            trx.add(R.id.fl_main_content, mFragmentList.get(index));
        }
        trx.show(mFragmentList.get(index)).commit();
    }

    private BaseTabItem newItem(int drawable, int checkDrawable) {
        OnlyIconMaterialItemView onlyIconItemView = new OnlyIconMaterialItemView(this);
        onlyIconItemView.initialization("", ContextCompat.getDrawable(this, drawable), ContextCompat.getDrawable(this, checkDrawable), false, R.color.colorAccent, R.color.colorPrimary);
        return onlyIconItemView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GoogleSuiteInstallState googleSuiteInstallState) {
        if (googleSuiteInstallState == null) return;
        if (googleSuiteInstallState == GoogleSuiteInstallState.GO) {
            mNavController.setSelect(0);
            select(0, 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        postGoogleSuiteState();
        postUninstallAppInfo();
    }

    private void postGoogleSuiteState() {
        GoogleSuiteInstallState googleSuiteInstallState = App.getApp().getGoogleSuiteInstallState();
        if (googleSuiteInstallState != null) {
            EventBus.getDefault().post(googleSuiteInstallState);
        }
    }

    private void postUninstallAppInfo() {
        AppInfo appInfo = App.getApp().getUninstallAppInfo();
        if (appInfo != null) {
            EventBus.getDefault().post(appInfo);
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

    private long firstTime = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return false;
        }
        long secondTime = System.currentTimeMillis();
        if (secondTime - this.firstTime > 1500) {
            ToastUtil.toast2(this, "再按一次退出程序");
            this.firstTime = secondTime;
            return true;
        }
        finish();
        return true;
    }

}
