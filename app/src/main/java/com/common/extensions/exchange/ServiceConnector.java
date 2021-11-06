package com.common.extensions.exchange;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class ServiceConnector implements ServiceConnection, ServiceInterface, ServiceInterface.Receiver {
    private static final Parcel empty = Parcel.obtain();
    private final ServiceReceiver delegate = new ServiceReceiver(this);
    private final WeakReference<Context> client;
    private final Class<? extends ServiceEngine> server;
    private IBinder service = null;
    private boolean bound = false;
    private int debug_regcounter = 0;
    private int debug_bindcounter = 0;

    public ServiceConnector(@NonNull Context client, @NonNull Class<? extends ServiceEngine> server) {
        super();
        this.client = new WeakReference<Context>(client);
        this.server = server;
    }

    public ServiceInterface.Receiver receiver() {
        return delegate.receiver();
    }

    @Override
    public int enqueue(@NonNull JobInfo work, Bundle params) {
        final Context context = client.get();

        if (context != null) {
            final Intent intent = work.asIntent(context, server);
            if (context.startService(intent.replaceExtras(params)) != null) return RESULT_SUCCESS;
        }
        return RESULT_FAILURE;
    }

    @Override
    public void cancelAll() {
        final Context context = client.get();

        if (context != null) {
            final Intent intent = new Intent(context, server);
            context.startService(intent);
        }
    }

    @Override
    public void cancel(@NonNull JobInfo work) throws RemoteException {
        try {
            final Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(work, 0);
            service.transact(ACTION_CANCEL, parcel, null, IBinder.FLAG_ONEWAY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Service unavailable");
        }
    }

    private List<JobInfo> joblist(int action) throws RemoteException {
        try {
            final Parcel parcel = Parcel.obtain();
            if (service.transact(action, empty, parcel, 0)) {
                final List<JobInfo> result = new ArrayList<JobInfo>(parcel.dataSize());
                parcel.readTypedList(result, JobInfo.CREATOR);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RemoteException("Service unavailable");
    }

    public boolean isConnected() {
        return (this.service != null);
    }

    @NonNull
    @Override
    public List<JobInfo> getAllPendingJobs() throws RemoteException {
        return joblist(ACTION_LISTALL);
    }

    @NonNull
    @Override
    public List<JobInfo> getStartedJobs() throws RemoteException {
        return joblist(ACTION_LISTRUN);
    }

    @Override // Bind link ESTABLISHED
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = service;
        // @Override method must run after
    }

    @Override // Bind link Temporarily unavailable (onServiceConnected will be called)
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
        // @Override method must run after
    }

    @Override // Bind link BROKEN
    public void onBindingDied(ComponentName name) {
        onServiceDisconnected(name);
        unbindService();
        // @Override method must run after
    }

    @Override // Binding Impossible
    public void onNullBinding(ComponentName name) {
    }

    public void registerService(boolean binding) {
        final Context context = client.get();
        debug_regcounter++;

        if (context != null) {
            delegate.registerReceiver();
            if (binding) bindService(Context.BIND_AUTO_CREATE);
        }
    }

    protected boolean bindService(int flags) {
        final Context context = client.get();
        debug_bindcounter++;

        if (context != null && !bound) {
            final Intent intent = new Intent(server.getName(), null, context, server);
            bound = context.bindService(intent, this, flags);
        }
        return bound;
    }

    protected void unbindService() {
        final Context context = client.get();
        debug_bindcounter--;

        if (context != null && bound) {
            context.unbindService(this);
            this.service = null;
            bound = false;
        }
    }

    public void unregisterService() {
        final Context context = client.get();
        debug_regcounter--;

        if (context != null) {
            if (bound) unbindService();
            context.stopService(new Intent(context, server));
            delegate.unregisterReceiver();

            if (debug_regcounter != 0 || debug_bindcounter != 0)
                throw new Error("Service registration balance mismatch");
        }
    }
}
