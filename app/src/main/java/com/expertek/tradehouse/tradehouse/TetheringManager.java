package com.expertek.tradehouse.tradehouse;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Wrapper for Hidden functionality of Android TetheringManager
 * path: "Android SDK/android/net/TetheringManager.java"
 * https://stackoverflow.com/questions/3436280/start-stop-built-in-wi-fi-usb-tethering-from-code
 * https://stackoverflow.com/questions/3635101/how-to-sign-android-app-with-system-signature/3651653
 */
public class TetheringManager {
    public static final String TETHERING_SERVICE = "tethering"; // from Context.java

    public static final int TETHERING_USB = 1; // from TetheringManager.java
    public static final int TETHERING_BLUETOOTH = 2; // from TetheringManager.java
    public static final int TETHERING_WIFI_P2P = 3; // from TetheringManager.java
    public static final int TETHERING_NCM = 4; // from TetheringManager.java
    public static final int TETHERING_ETHERNET = 5; // from TetheringManager.java

    private final Object tetheringManagerImpl;

    public TetheringManager(Object tetheringManagerImpl) {
        this.tetheringManagerImpl = tetheringManagerImpl;
    }

    protected Object getImpl() {
        return tetheringManagerImpl;
    }

    @SuppressWarnings("ConstantConditions")
    protected boolean isTetheringSupported() {
        try {
            final Method isTetheringSupported = tetheringManagerImpl
                    .getClass().getMethod("isTetheringSupported");
            isTetheringSupported.setAccessible(true);
            return (Boolean) isTetheringSupported.invoke(tetheringManagerImpl);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    protected String[] getTetherableIfaces() {
        try {
            final Method getTetherableIfaces = tetheringManagerImpl
                    .getClass().getMethod("getTetherableIfaces");
            getTetherableIfaces.setAccessible(true);
            return (String[]) getTetherableIfaces.invoke(tetheringManagerImpl);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    protected String[] getTetherableUsbRegexs() {
        try {
            final Method getTetherableUsbRegexs = tetheringManagerImpl
                    .getClass().getMethod("getTetherableUsbRegexs");
            getTetherableUsbRegexs.setAccessible(true);
            return (String[]) getTetherableUsbRegexs.invoke(tetheringManagerImpl);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    protected int tether() {
        try {
            final Method tether = tetheringManagerImpl
                    .getClass().getMethod("tether");
            tether.setAccessible(true);
            return (int) tether.invoke(tetheringManagerImpl);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    protected int untether() {
        try {
            final Method untether = tetheringManagerImpl
                    .getClass().getMethod("untether");
            untether.setAccessible(true);
            return (int) untether.invoke(tetheringManagerImpl);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    protected int setUsbTethering(boolean enable) {
        try {
            final Method setUsbTethering = tetheringManagerImpl
                    .getClass().getMethod("setUsbTethering", Boolean.TYPE);
            setUsbTethering.setAccessible(true);
            return (int) setUsbTethering.invoke(tetheringManagerImpl, enable);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    // ConnectivityManager wrapper
    public static abstract class OnStartTetheringCallback {
        public void onTetheringStarted() {}
        public void onTetheringFailed() {}
    }

    // ConnectivityManager wrapper
    protected void startTethering(int type, boolean showProvisioningUi, final OnStartTetheringCallback callback) {
        try {
            Class<?> onStartTetheringCallback = null;
            for (Class<?> cls : tetheringManagerImpl.getClass().getDeclaredClasses()) {
                if (cls.getName().contains("OnStartTetheringCallback")) {
                    onStartTetheringCallback = cls;
                    break;
                }
            }

            final Method startTethering = tetheringManagerImpl
                    .getClass().getMethod("startTethering", Integer.TYPE, Boolean.TYPE,
                            onStartTetheringCallback);
            startTethering.setAccessible(true);
            startTethering.invoke(tetheringManagerImpl, type, showProvisioningUi, callback);
        } catch (ReflectiveOperationException e) {
        }
    }

    public interface StartTetheringCallback {
        default void onTetheringStarted() {}
        default void onTetheringFailed(final int error) {}
    }

    protected void startTethering(int type, Executor executor, StartTetheringCallback callback) {
        try {
            Class<?> StartTetheringCallback = null;
            for (Class<?> cls : tetheringManagerImpl.getClass().getDeclaredClasses()) {
                if (cls.getName().contains("StartTetheringCallback")) {
                    StartTetheringCallback = cls;
                    break;
                }
            }

            final Method startTethering = tetheringManagerImpl
                    .getClass().getMethod("startTethering", Integer.TYPE, Executor.class,
                            StartTetheringCallback);
            startTethering.setAccessible(true);
            startTethering.invoke(tetheringManagerImpl, type, executor, callback);
        } catch (ReflectiveOperationException e) {
        }
    }

    public void stopTethering(int type) {
        try {
            final Method stopTethering = tetheringManagerImpl
                    .getClass().getMethod("stopTethering", Integer.TYPE);
            stopTethering.setAccessible(true);
            stopTethering.invoke(tetheringManagerImpl, type);
        } catch (ReflectiveOperationException e) {
        }
    }
}