package com.yc.google.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kk.utils.ScreenUtil;
import com.yc.google.R;

import butterknife.BindView;

public class RepairAdCheckStatusButton extends BaseView {
    @BindView(R.id.textView_repairAdCheckButton)
    TextView mRepairCheckTextView;

    @BindView(R.id.checkbox_repairAdCheckButton)
    CheckBox mRepairCheckBox;

    public RepairAdCheckStatusButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_repair_ad_check_status_button;
    }

    @Override
    protected void initViews() {
        setMinimumWidth(ScreenUtil.dip2px(mContent, 60));
        setMinimumHeight(ScreenUtil.dip2px(mContent, 24));
        if (isInEditMode()) {
            this.mRepairCheckTextView.setVisibility(View.INVISIBLE);
            this.mRepairCheckBox.setVisibility(View.VISIBLE);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
