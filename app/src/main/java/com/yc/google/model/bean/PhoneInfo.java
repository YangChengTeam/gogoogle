package com.yc.google.model.bean;

public class PhoneInfo {
    private int sdkVersion;
    private String guid;
    private boolean isCpu64;
    private boolean isRoot;
    private String brand;
    private String androidVersion;
    private String packages;
    private String rom;
    private int dip;
    private boolean x86;

    public boolean isX86() {
        return x86;
    }

    public void setX86(boolean x86) {
        this.x86 = x86;
    }

    public int getDip() {
        return dip;
    }

    public void setDip(int dip) {
        this.dip = dip;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public int getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(int sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isCpu64() {
        return isCpu64;
    }

    public void setCpu64(boolean cpu64) {
        isCpu64 = cpu64;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }
}
