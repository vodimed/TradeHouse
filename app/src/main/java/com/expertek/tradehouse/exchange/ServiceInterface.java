package com.expertek.tradehouse.exchange;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
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
    String THROWABLE = "#";

    interface Receiver {
        void onServiceResult(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle result);
        void onServiceException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e);
    }

    int enqueue(@NonNull JobInfo work, Bundle params);
    void cancelAll();
    void cancel(@NonNull JobInfo work) throws RemoteException;
    @NonNull List<JobInfo> getAllPendingJobs() throws RemoteException;
    @NonNull public List<JobInfo> getStartedJobs() throws RemoteException;
    //void onLogEvent(int priority, String tag, String message) throws RemoteException;

    /**
     * Method Call(): true = result, false = no result; exception = error
     * Method onCancel(): true = cancellable; false = cancel not supported,
     * if onCancel() = false, working thread might be force terminated anyway.
     * Usually onCancel sets volatile boolean flag, checking in Call() method
     */
    interface Task extends Callable<Boolean> {
        void onCreate(@Nullable Bundle params, @Nullable Bundle result) throws Exception;
        void onDestroy() throws Exception;
        boolean onCancel();
    }

    /**
     * JobScheduler-compatible format. Parcelable code is
     * AUTO-GENERATED ("Add Parcelable Implementation")
     */

    class JobInfo implements Parcelable {
        protected final int jobId;
        protected final int resId;
        protected final ComponentName process;
        protected Parcelable extra = null;

        // Not auto-generated
        public JobInfo(int jobId, @NonNull Class<? extends Task> cls, @Nullable Receiver res) {
            this.jobId = jobId;
            this.resId = (res != null ? res.hashCode() : 0);
            this.process = new ComponentName(cls.getPackage().getName(), cls.getCanonicalName());
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

        protected @Nullable Class<? extends Task> getTask() throws ClassNotFoundException {
            return Class.forName(process.getClassName()).asSubclass(Task.class);
        }

        // Not auto-generated
        protected JobInfo(Intent intent) {
            this.jobId = intent.getSourceBounds().left;
            this.resId = intent.getSourceBounds().right;
            this.process = new ComponentName(intent.getPackage(), intent.getScheme());
        }

        // Not auto-generated
        public Intent asIntent(@NonNull Context client, @NonNull Class<?> server) {
            /* To use ServiceEngine with JobScheduler add the following line to JobInfo.Builder specification
             * << jobInfoItem.setClipData(new ClipData("", new String[0], new ClipData.Item(intent)), 0); >>
             */
            final Intent intent = new Intent(client, server);
            intent.setSourceBounds(new Rect(jobId, 0, resId, 0));
            intent.setPackage(process.getPackageName());
            intent.setData(new Uri.Builder().scheme(process.getClassName()).opaquePart("").build());
            return intent;
        }
    }
}
