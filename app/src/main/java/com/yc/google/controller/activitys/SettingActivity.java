package com.yc.google.controller.activitys;

import android.preference.Preference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.kk.securityhttp.domain.GoagalInfo;
import com.kk.utils.PreferenceUtil;
import com.yc.google.R;
import com.yc.google.constant.Config;

import butterknife.BindView;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.toolbar_setting)
    Toolbar mToolbar;

    @BindView(R.id.switch_setting_net)
    SwitchCompat m4gSwitchCompat;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        m4gSwitchCompat.setChecked(PreferenceUtil.getImpl(this).getBoolean(Config.SWITCH_4G, false));
        m4gSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtil.getImpl(SettingActivity.this).putBoolean(Config.SWITCH_4G, b);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
