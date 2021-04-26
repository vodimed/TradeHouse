package com.expertek.tradehouse.obsolete;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

public abstract class ServiceCommunicator_ extends ServiceMessenger_ {
    // Messenger for communicating with service.
    private final Context mClient;
    private final Class<?> mSClass;
    private Messenger mService = null;
    private boolean mEngaged = false;

    public ServiceCommunicator_(@NonNull Context client, Class<?> server) {
        mClient = client;
        mSClass = server;
    }

            /*
            try {
                final IntentFilter filter = new IntentFilter();
                filter.addDataType(TradeHouseAction.class.getCanonicalName() + "/x");
                filter.addDataScheme(TradeHouseAction.class.getPackage().getName());

                context.registerReceiver(receiver, filter);

            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
            */
    /*
        final JobScheduler mScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final ComponentName service = new ComponentName(this, TradeHouseService.class);
        for (int jobID = 1; jobID < 10; ++jobID) {
            final JobInfo.Builder jobInfo = new JobInfo.Builder(jobID, service).setOverrideDeadline(0);
            final Bundle params = new Bundle();

            final Intent intent = new ServiceInterface.JobInfo(jobID, TradeHouseAction.class).asIntent(this, TradeHouseService.class);
            intent.putExtras(params);

            jobInfo.setClipData(new ClipData("", new String[0], new ClipData.Item(intent)), 0);
            int result = mScheduler.enqueue(jobInfo.build(), new JobWorkItem(new Intent()));
        }
        final List<JobInfo> jobList = mScheduler.getAllPendingJobs();
    */
    /*
    private void scheduleJob() {
        if (mScheduler == null) mScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobService == null) jobService = new ComponentName(this, TradeHouseService.class);

        for (int i = 0; i < 10; ++i) {
            ++jobID;

            final JobInfo.Builder jobInfo = new JobInfo.Builder(jobID, jobService).setOverrideDeadline(0);
            final PersistableBundle pb = new PersistableBundle();
            final Bundle b = new Bundle();

            //pb.putString("pb", "A");
            //b.putString("b", "B");
            jobInfo.setExtras(pb);
            jobInfo.setTransientExtras(b);

            final Intent intent = new Intent(String.valueOf(jobID));
            intent.replaceExtras("in", String.valueOf(jobID));

            int result = mScheduler.enqueue(jobInfo.build(), new JobWorkItem(intent));
            //int result = mScheduler.schedule(jobInfo.build());
            Log.d(TradeHouseService.class.getSimpleName(), "Planned " + result);

            getAllJobs();
            tradehouse.bindService();
            getAllJobs();
        }
    */
    /*
        ResultReceiver r = intent.getParcelableExtra("r");
        Bundle b = new Bundle();
        b.putString("l", "Hi!");
        r.send(1, b);

    private ResultReceiver result = new Receiver(this);
    private static class Receiver extends ResultReceiver {
        private final ServiceInterface dest;

        public Receiver(ServiceInterface dest) {
            super(null);
            this.dest = dest;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            dest.onReceiveResult(resultCode, resultData);
        }
    }
    */

    // Establish a connection with the service.  We use an explicit
    // class name because there is no reason to be able to let other
    // applications replace our component.
    public boolean bindService() {
        final Intent intent = new Intent("msg", null, mClient, mSClass);
        mClient.startService(intent);
        return mClient.bindService(intent, mConnection, 0);
    }

    // If bindService has been called previously and has returned true,
    // then now is the time to unregister. We remove as  well all pending
    // income messages from service before unbinding.
    public void unbindService() {
        clearLocalQueue();
        //mClient.unbindService(mConnection);
        mClient.stopService(new Intent(mClient, mSClass));
    }

    protected abstract void onServiceConnected();
    //protected abstract void onServiceDisconnected();
    //protected abstract void onBindingDied();
    protected abstract boolean onIncomingEvent(int what, int arg1, int arg2, Object obj);

    @Override
    protected boolean onIncomingEvent(int what, int arg1, int arg2, Object obj, Messenger replyTo) {
        return onIncomingEvent(what, arg1, arg2, obj);
    }

    public void postInquiry(int what, int arg1, int arg2, Object obj) throws RemoteException {
        if (mService == null) throw new RemoteException();
        postInquiry(mService, what, arg1, arg2, obj);
    }

    public void clearRemoteQueue(int arg1, int arg2) throws RemoteException {
        if (mService == null) throw new RemoteException();
        mService.send(Message.obtain(null, MSG_CLEAR_QUEUE, arg1, arg2));
    }

    public void inquireRemoteQueue() throws RemoteException {
        if (mService == null) throw new RemoteException();
        mService.send(Message.obtain(null, MSG_PRINT_QUEUE, 0, 0));
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            ServiceCommunicator_.this.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            //ServiceLink.this.onServiceDisconnected();
        }

        @Override
        public void onBindingDied(ComponentName name) {
            mService = null;
            //ServiceLink.this.onBindingDied();
        }
    };
}
