package com.common.extensions;

import android.util.Log;

import com.expertek.tradehouse.Application;

public class Logger {
    private static final int idx = 1;
    private static final boolean show = true;

    public static void e(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.e(caller, e.getMessage(), e);
        if (show) Dialogue.Error(Application.app(), e);
    }

    public static void w(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.w(caller, e.getMessage(), e);
        if (show) Dialogue.Error(Application.app(), e);
    }

    public static void i(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.i(caller, e.getMessage(), e);
        if (show) Dialogue.Error(Application.app(), e);
    }
}
