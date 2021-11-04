package com.obsolete;

import android.util.Log;

import androidx.annotation.StringRes;

import com.expertek.tradehouse.Application;

public final class HandleException_ {
    public HandleException_(Exception e) {
        this(e, 0);
    }

    public HandleException_(Exception e, @StringRes int id) {
        accept(e, id);
    }

    public HandleException_(Exception e, String msg) {
        accept(e, msg);
    }

    public static void accept(Exception e, @StringRes int id) {
        accept(e, Application.app().getResources().getString(id));
    }

    public static void accept(Exception e, String msg) {
        final String message = (msg != null ? msg : e.getMessage());
        //Toast.makeText(MainApplication.getApplication(), message, Toast.LENGTH_SHORT).show();
        Log.e("HandleException_", message);
    }

    /*
    private static String getLocation() {
        final String className = ExceptionHandler.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        final String className = (clazz != null ? clazz.getSimpleName() : "");
        if (className != null && !className.isEmpty()) return className;
        return getClassName(clazz.getEnclosingClass());
    }
    */
}
