package com.yc.google.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kk.utils.ScreenUtil;
import com.yc.google.R;

import butterknife.BindView;

public class CheckStatusButton extends BaseView {

    private String packageName;

    @BindView(R.id.checkStatusButton_text)
    TextView checkStatusTextView;

    @BindView(R.id.checkStatusButton_image)
    ImageView checkStatusImageView;

    @BindView(R.id.checkStatusButton_progress)
    ProgressBar checkStatusProcessBar;

    public CheckStatusButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_check_status_button;
    }

    @Override
    protected void initViews() {
        setMinimumWidth(ScreenUtil.dip2px(mContent, 60));
        setMinimumHeight(ScreenUtil.dip2px(mContent, 24));
    }

    public void setSuccessStatus(){
        this.checkStatusTextView.setVisibility(View.GONE);
        this.checkStatusImageView.setVisibility(View.VISIBLE);
        this.checkStatusProcessBar.setVisibility(View.GONE);
        this.checkStatusImageView.setImageResource(R.drawable.ic_check_circle_green_24dp);
    }

    public void setFailStatus(){
        this.checkStatusTextView.setVisibility(View.GONE);
        this.checkStatusImageView.setVisibility(View.VISIBLE);
        this.checkStatusProcessBar.setVisibility(View.GONE);
        this.checkStatusImageView.setImageResource(R.drawable.ic_error_red_24dp);
    }

    public void setProcessStatus(){
        this.checkStatusTextView.setVisibility(View.GONE);
        this.checkStatusProcessBar.setVisibility(View.VISIBLE);
        this.checkStatusImageView.setVisibility(View.GONE);
    }

    public void setTextStatus(String percentage){
        this.checkStatusTextView.setVisibility(View.VISIBLE);
        this.checkStatusImageView.setVisibility(View.GONE);
        this.checkStatusProcessBar.setVisibility(View.GONE);
        this.checkStatusTextView.setText(percentage);
    }


    public void setPackageName(String str) {
        this.packageName = str;
    }

    public String getPackageName() {
        return this.packageName;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
