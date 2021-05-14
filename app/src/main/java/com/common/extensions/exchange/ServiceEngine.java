package com.common.extensions.exchange;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobServiceEngine;
import android.app.job.JobWorkItem;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.expertek.tradehouse.tradehouse.Настройки;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        Random rand = new Random();
        for (int i = 1, j = 17; i < j; i++) {
            result.add(new JobInfo(i, Настройки.class, null));
        }
        try {
            result.remove(rand.nextInt(7));
            result.remove(rand.nextInt(6));
        } catch (Exception e) {}
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
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
                return false;
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
                final JobWorkItem item = params.dequeueWork();
                if (item == null) return false; // recursion finished

                final Intent intent = item.getIntent();
                Bundle parameters = intent.getExtras();

                if (parameters == null) {
                    parameters = params.getTransientExtras();
                } else if (params.getTransientExtras() != null) {
                    parameters.putAll(params.getTransientExtras());
                }

                if (parameters == null) {
                    parameters = new Bundle(params.getExtras());
                } else if (params.getTransientExtras() != null) {
                    parameters.putAll(params.getExtras());
                }

                final JobInfo jobInfo = new JobInfo(intent);
                jobInfo.extra = new Pair<JobParameters, JobWorkItem>(params, item);

                if (service.enqueueResult(jobInfo, parameters, this) == RESULT_FAILURE)
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

            @SuppressWarnings("unchecked") // packed in onStartJob()
            final JobParameters params = ((Pair<JobParameters, JobWorkItem>) work.extra).first;
            @SuppressWarnings("unchecked") // packed in onStartJob()
            final JobWorkItem item = ((Pair<JobParameters, JobWorkItem>) work.extra).second;

            params.completeWork(item);
            onStartJob(params); // recursive call
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {
            // Nothing: Controller packs all exceptions into the result, see onServiceResult()
        }
    }
}
