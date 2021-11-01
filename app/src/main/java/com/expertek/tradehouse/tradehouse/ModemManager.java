package com.expertek.tradehouse.tradehouse;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;

public class ModemManager {
    private final ConnectivityManager connectivityManager;
    private final TetheringManager tetheringManager;

    //@SuppressLint("WrongConstant")
    public ModemManager(Context context) {
        this.connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //tetheringManagerImpl =
        //        context.getSystemService(TetheringManager.TETHERING_SERVICE);
        //tetheringManagerImpl = getTetheringManager();
        this.tetheringManager = new TetheringManager(connectivityManager);

    }

    private Object getTetheringManager() {
        try {
            final Field mTetheringManager = connectivityManager.
                    getClass().getDeclaredField("mTetheringManager");
            mTetheringManager.setAccessible(true);
            return mTetheringManager.get(connectivityManager);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public boolean isEnabledUSB() {
        if (!tetheringManager.isTetheringSupported()) return false;
        final String[] ifaces = tetheringManager.getTetherableIfaces();
        final String[] mask = tetheringManager.getTetherableUsbRegexs();
        return ((ifaces.length > 0) && (mask.length > 0));
    }

    public void setUsbTethering(boolean enable) {
        if (enable) {
            if (tetheringManager.getImpl() instanceof ConnectivityManager) {
                tetheringManager.startTethering(TetheringManager.TETHERING_USB, false, null);
            } else {
                tetheringManager.startTethering(TetheringManager.TETHERING_USB, tetherExecutor, null);
            }
        } else {
            tetheringManager.stopTethering(TetheringManager.TETHERING_USB);
        }
    }

    private static final Executor tetherExecutor = new Executor() {
        @Override
        public void execute(Runnable command) {
            System.out.println("Executor!");
        }
    };

    private static final TetheringManager.OnStartTetheringCallback oncallback =
            new TetheringManager.OnStartTetheringCallback()
    {
    };

    private static final TetheringManager.StartTetheringCallback callback =
            new TetheringManager.StartTetheringCallback()
    {
    };
}
