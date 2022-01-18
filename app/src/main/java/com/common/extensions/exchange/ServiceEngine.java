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

import com.common.extensions.Logger;

import java.util.List;

public class ServiceEngine extends Service implements ServiceInterface, ServiceInterface.Receiver {
    private final ServiceQueue queue = new ServiceQueue();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int result = super.onStartCommand(intent, flags, startId);

        if (intent.getSourceBounds() == null) {
            cancelAll(); // if no jobId, i.e. getSourceBounds()
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
        queue.cancelAll();
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        queue.cancel(new JobInfo(rootIntent));
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onJobResult(@NonNull JobInfo work, @Nullable Bundle result) {
        final Intent intent = work.asIntent(this, ServiceReceiver.class);
        intent.replaceExtras(result);
        sendBroadcast(intent);
    }

    @Override
    public void onJobException(@NonNull JobInfo work, @NonNull Throwable e) {
        final Bundle result = new Bundle();
        result.putSerializable(ServiceInterface.EXCEPTION, e);
        onJobResult(work, result);
    }

    public int enqueueResult(@NonNull JobInfo work, Bundle params, Receiver receiver) {
        try {
            queue.enqueue(work, params, receiver);
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
        queue.cancelAll();
    }

    @Override
    public void cancel(@NonNull JobInfo work) throws RemoteException {
        queue.cancel(work);
    }

    @NonNull
    @Override
    public List<JobInfo> getAllPendingJobs() throws RemoteException {
        return queue.listAwaiting();
    }

    @NonNull
    @Override
    public List<JobInfo> getStartedJobs() throws RemoteException {
        return queue.listExecuting();
    }

    @NonNull
    @Override
    public List<String> getProgress(@NonNull JobInfo work, int since) {
        return queue.getProgress(work, since);
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
                    case ACTION_PROGRESS:
                        assert reply != null;
                        reply.writeStringList(service.getProgress(
                                data.readTypedObject(JobInfo.CREATOR),
                                data.readInt()));
                        return true;
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
                Logger.e(e);
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
                service.cancel(new JobInfo(params.getJobId(), ServiceTask.class, null));
            } catch (RemoteException e) {
                Logger.w(e);
            }
            return false; // false = acyclic; true = reshedule
        }

        @Override
        public void onJobResult(@NonNull JobInfo work, @Nullable Bundle result) {
            service.onJobResult(work, result);

            @SuppressWarnings("unchecked") // packed in onStartJob()
            final JobParameters params = ((Pair<JobParameters, JobWorkItem>) work.extra).first;
            @SuppressWarnings("unchecked") // packed in onStartJob()
            final JobWorkItem item = ((Pair<JobParameters, JobWorkItem>) work.extra).second;

            params.completeWork(item);
            onStartJob(params); // recursive call
        }

        @Override
        public void onJobException(@NonNull JobInfo work, @NonNull Throwable e) {
            final Bundle result = new Bundle();
            result.putSerializable(ServiceInterface.EXCEPTION, e);
            onJobResult(work, result);
        }
    }
}
