package com.expertek.tradehouse.exchange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Needs registration in AndroidManifest.xml file:
 *
 * <receiver
 *     android:name=".exchange.ServiceReceiver"
 *     android:enabled="true"
 *     android:exported="false" />
 */
public final class ServiceReceiver extends BroadcastReceiver {
    protected final ServiceInterface.Receiver context;

    private static final ArrayMap<Integer, WeakReference<ServiceReceiver>> pool =
            new ArrayMap<Integer, WeakReference<ServiceReceiver>>(5);

    /**
     * Do not use this constructor: system-call only. Instead,
     * implement ServiceInterface.Receiver interface in your Activity
     * or nested subclass and pass it to constructor(dispatcher) parameter
     */
    public ServiceReceiver() {
        this.context = null;
    }

    // Implement ServiceInterface.Receiver interface in your Activity or nested subclass
    public ServiceReceiver(@NonNull ServiceInterface.Receiver context) {
        this.context = context;
        if (context != null) registerReceiver();
    }

    @Override
    protected void finalize() throws Throwable {
        if (context != null) unregisterReceiver();
        super.finalize();
    }

    public ServiceInterface.Receiver receiver() {
        return context;
    }

    protected void registerReceiver() {
        synchronized (pool) {
            pool.put(context.hashCode(), new WeakReference<ServiceReceiver>(this));
        }
    }

    protected void unregisterReceiver() {
        synchronized (pool) {
            pool.remove(context.hashCode());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceReceiver receiver = null;
        synchronized (pool) {
            final WeakReference<ServiceReceiver> value = pool.get(intent.getSourceBounds().right);

            if (value != null) {
                receiver = value.get();
                if (receiver == null) pool.remove(intent.getSourceBounds().right);
            }
        }

        if (receiver != null) {
            final ServiceInterface.JobInfo jobInfo = new ServiceInterface.JobInfo(intent);
            final Bundle result = intent.getExtras();

            if (result != null && result.containsKey(ServiceInterface.THROWABLE)) {
                final Throwable e = (Throwable)result.getSerializable(ServiceInterface.THROWABLE);
                receiver.context.onServiceException(jobInfo, e);
            } else {
                receiver.context.onServiceResult(jobInfo, result);
            }
        }
    }
}
