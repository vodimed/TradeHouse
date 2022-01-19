package com.common.extensions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static final int idx = 1;
    private static WeakReference<Context> context = null;

    public static void setApplicationContext(android.app.Application application) {
        application.registerActivityLifecycleCallbacks(lifecycle);
    }

    public static void e(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.e(caller, e.getMessage(), e);
        if (context != null) Dialogue.Error(context.get(), e);
    }

    public static void w(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.w(caller, e.getMessage(), e);
        if (context != null) Dialogue.Error(context.get(), e);
    }

    public static void i(Throwable e) {
        final String caller = Thread.currentThread().getStackTrace()[idx].getClassName();
        Log.i(caller, e.getMessage(), e);
        if (context != null) Dialogue.Error(context.get(), e);
    }

    // You need permissions in AndroidManifest.xml file:
    // <uses-permission android:name="android.permission.READ_LOGS"/>
    public static List<String> getMessages() {
        final ArrayList<String> result = new ArrayList<String>(10);

        final Process process;
        try {
            process = Runtime.getRuntime().exec("logcat -d -b main -v time -s *:E");
        } catch (IOException e) {
            Logger.e(e);
            return result;
        }

        final BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
        } catch (IllegalArgumentException e) {
            Logger.e(e);
            return result;
        }

        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.add(line);
            }
        } catch (IOException e) {
            Logger.e(e);
        }
        return result;
    }

    /**
     * Activities Lifecycle tracking system
     */
    private static final android.app.Application.ActivityLifecycleCallbacks lifecycle =
            new android.app.Application.ActivityLifecycleCallbacks()
    {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            context = new WeakReference<Context>(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    };
}
