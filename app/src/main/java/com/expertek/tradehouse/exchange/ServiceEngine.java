package com.expertek.tradehouse.exchange;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobServiceEngine;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class ServiceEngine extends Service implements ServiceInterface, ServiceInterface.Receiver {
    private final ServiceQueue queue = new ServiceQueue();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int result = super.onStartCommand(intent, flags, startId);

        if (intent.getSourceBounds() == null) {
            cancelAll();
        } else {
            enqueue(new JobInfo(intent), intent.getExtras());
        }
        return result;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction() != null) {
            return new TransactionBinder(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new JobScheduleFacility(this).getBinder();
        } else {
            return null;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent); // TODO: Cancel asynchronous running transactions
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent); // TODO: Notify all running tasks on being killed
    }

    @Override
    public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
        final Intent intent = work.asIntent(this, ServiceReceiver.class);
        intent.replaceExtras(result);
        sendBroadcast(intent);
    }

    @Override
    public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {
        // Nothing: Controller packs all exceptions into the result, see onServiceResult()
    }

    public int enqueueResult(@NonNull JobInfo work, Bundle params, Receiver receiver) {
        try {
            queue.enqueue(new ServiceQueue.Controller(work, params, receiver));
            return RESULT_SUCCESS;
        } catch (ReflectiveOperationException e) {
            return RESULT_FAILURE;
        }
    }

    @Override
    public int enqueue(@NonNull JobInfo work, Bundle params) {
        return enqueueResult(work, params, this);
    }

    @Override
    public void cancelAll() {

    }

    @Override
    public void cancel(@NonNull JobInfo work) throws RemoteException {

    }

    @NonNull
    @Override
    public List<JobInfo> getAllPendingJobs() throws RemoteException {
        ArrayList<JobInfo> result = new ArrayList<JobInfo>();
        result.add(new JobInfo(-1, null, null));
        return result;
    }

    @NonNull
    @Override
    public List<JobInfo> getStartedJobs() throws RemoteException {
        return null;
    }

    private static class TransactionBinder extends Binder {
        private final ServiceInterface service;

        public TransactionBinder(ServiceInterface service) {
            super();
            this.service = service;
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case ACTION_CANCEL:
                    service.cancel(data.readTypedObject(JobInfo.CREATOR));
                    return true;
                case ACTION_LISTALL:
                    assert reply != null;
                    reply.writeTypedList(service.getAllPendingJobs());
                    return true;
                case ACTION_LISTRUN:
                    assert reply != null;
                    reply.writeTypedList(service.getStartedJobs());
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static class JobScheduleFacility extends JobServiceEngine implements ServiceInterface.Receiver {
        private final ServiceEngine service;

        public JobScheduleFacility(ServiceEngine service) {
            super(service);
            this.service = service;
        }

        @Override
        public boolean onStartJob(JobParameters params) {
            try {
                final Intent intent = params.getClipData().getItemAt(0).getIntent();
                final JobInfo jobInfo = new JobInfo(intent);
                jobInfo.extra = params;
                if (service.enqueueResult(jobInfo, intent.getExtras(), this) == RESULT_FAILURE)
                    return false;
            } catch (Exception e) {
                return false;
            }
            return true; // false = finished; true = asynchronous running
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            try {
                service.cancel(new JobInfo(params.getJobId(), Task.class, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false; // false = acyclic; true = reshedule
        }

        @Override
        public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
            service.onServiceResult(work, result);
            jobFinished((JobParameters)work.extra, false);
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {
            // Nothing: Controller packs all exceptions into the result, see onServiceResult()
        }
    }
}
