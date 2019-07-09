package com.yc.google.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.ButterKnife;

public abstract class BaseView extends FrameLayout {

    protected Context mContent;
    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(getLayoutId(), this, true);
        ButterKnife.bind(this);
        mContent = context;
    }

    protected abstract int  getLayoutId();
    protected abstract void initViews();
}
