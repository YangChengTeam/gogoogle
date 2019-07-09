package com.yc.google.model.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yc.google.R;

public class StatusInfo implements MultiItemEntity {
    private String title;
    private String desp;
    private String action;
    private int itemType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

}
