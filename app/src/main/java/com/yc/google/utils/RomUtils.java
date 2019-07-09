package com.yc.google.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RomUtils {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";
    private static Properties mProper;

    static {
        if (mProper == null) {
            mProper = new Properties();
        }
        try {
            mProper.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMIUI() {
        return mProper.containsKey(KEY_MIUI_VERSION_CODE) || mProper.containsKey(KEY_MIUI_VERSION_NAME);
    }

    public static boolean isEMUI() {
        return mProper.containsKey(KEY_EMUI_VERSION_CODE);
    }

    public static boolean isFlyme() {
        return mProper.containsKey(KEY_FLYME_ICON_FALG) || mProper.containsKey(KEY_FLYME_SETUP_FALG) || mProper.containsKey(KEY_FLYME_PUBLISH_FALG);
    }

    public static String getRomInfo() {
        if (isMIUI()) {
            return "MIUI";
        } else if (isFlyme()) {
            return "Flyme";
        } else if (isEMUI()) {
            return "EMUI";
        } else {
            return "Other";
        }
    }
}


