package com.common.extensions.exchange;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

public interface ServiceInterface {
    int ACTION_CANCEL = -1;
    int ACTION_LISTALL = 0;
    int ACTION_LISTRUN = 1;
    int RESULT_FAILURE = 0;
    int RESULT_SUCCESS = 1;
    String EXCEPTION = "#";

    int enqueue(@NonNull JobInfo work, Bundle params);
    void cancelAll();
    void cancel(@NonNull JobInfo work) throws RemoteException;
    @NonNull List<JobInfo> getAllPendingJobs() throws RemoteException;
    @NonNull List<JobInfo> getStartedJobs() throws RemoteException;
    //void onLogEvent(int priority, String tag, String message) throws RemoteException;

    /**
     * JobScheduler-compatible format. Parcelable code is
     * AUTO-GENERATED ("Add Parcelable Implementation")
     */
    class JobInfo implements Parcelable {
        protected final int jobId;
        protected final int resId;
        protected final ComponentName process;
        protected Object extra = null; // @NonParcelField

        // Not auto-generated
        public JobInfo(int jobId, @NonNull Class<? extends ServiceTask> task, @Nullable Receiver result) {
            this.jobId = jobId;
            this.resId = (result != null ? result.hashCode() : 0);
            final Package pkg = task.getPackage();
            this.process = new ComponentName((pkg != null ? pkg.getName() : null), task.getName());
        }

        protected JobInfo(Parcel in) {
            jobId = in.readInt();
            resId = in.readInt();
            process = in.readParcelable(ComponentName.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(jobId);
            dest.writeInt(resId);
            dest.writeParcelable(process, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<JobInfo> CREATOR = new Creator<JobInfo>() {
            @Override
            public JobInfo createFromParcel(Parcel in) {
                return new JobInfo(in);
            }

            @Override
            public JobInfo[] newArray(int size) {
                return new JobInfo[size];
            }
        };

        // Not auto-generated
        public int getJobId() {
            return jobId;
        }

        // Not auto-generated
        public int hashCode() {
            return 31 * jobId + process.hashCode();
        }

        // Not auto-generated
        protected Class<? extends ServiceTask> getTask() throws ClassNotFoundException {
            return Class.forName(process.getClassName()).asSubclass(ServiceTask.class);
        }

        // Not auto-generated
        protected JobInfo(@NonNull Intent intent) {
            this.jobId = intent.getSourceBounds().left;
            this.resId = intent.getSourceBounds().right;
            this.process = new ComponentName(intent.getPackage(), intent.getAction());
        }

        // Not auto-generated
        public Intent asIntent(@NonNull Context context, @NonNull Class<?> server) {
            final Intent intent = new Intent(context, server);
            intent.setSourceBounds(new Rect(jobId, 0, resId, 0));
            intent.setPackage(process.getPackageName()).setAction(process.getClassName());
            return intent;
        }
    }

    /**
     * Task prototype (interface) for creation of user-defined classes.
     * Methods onCreate(), onCancel() and onDestroy() are executed from
     * the Main thread. Methods Call() is executed from the working thread.
     * If onCancel() throws exception, it means that the task does not
     * support cancel operation, but it will be force terminated anyway
     * by the service system mechanisms.
     */
    interface ServiceTask extends Callable<Bundle> {
        void onCreate(@Nullable Bundle params) throws Exception;
        void onCancel() throws Exception; // UnsupportedOperationException()
        void onDestroy() throws Exception;
    }

    /**
     * Service interface to receive results of Job execution
     */
    interface Receiver {
        void onJobResult(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle result);
        void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e);
    }
}
