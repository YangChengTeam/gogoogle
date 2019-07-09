package com.yc.google.controller.activitys;

import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.kk.securityhttp.domain.GoagalInfo;
import com.yc.google.App;
import com.yc.google.R;

import butterknife.BindView;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar_about)
    Toolbar mToolbar;

    @BindView(R.id.tv_about_version)
    TextView mAboutTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
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
        mAboutTextView.setText(TextUtils.concat(new CharSequence[]{"V", GoagalInfo.get().packageInfo.versionName}));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
