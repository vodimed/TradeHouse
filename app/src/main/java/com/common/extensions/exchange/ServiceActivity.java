package com.common.extensions.exchange;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.common.extensions.AdapterTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Service control center: add the following code to your Serice extends ServiceEngine
 * (permissions: <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />)
 * @Override
 * public void onCreate() {
 *      super.onCreate();
 *      try {
 *          startForeground(R.string.service_tradehouse, MainApplication.createNotification(
 *              ServiceActivity.class, R.string.trayTitle, R.string.trayMessage);
 *      } catch (Exception e) {
 *          e.printStackTrace();
 *      }
 * }
 * @Override
 * public void onDestroy() {
 *      try {
 *          stopForeground(false);
 *      } catch (Exception e) {
 *          e.printStackTrace();
 *      }
 *      super.onDestroy();
 * }
 */
public class ServiceActivity extends Activity {
    enum ServiceState {disabled, disconnected, connected}
    private final TaskList tasklist = new TaskList();
    private Timer autorefresh = null;
    private ActivityLayout activity = null;
    private ServiceConnector service = null;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ActivityLayout(this));
        //setContentView(R.layout.service_activity);

        activity = (ActivityLayout) this.getActivityLayout();
        activity.listActions.setAdapter(new TaskAdapter(this, AdapterLayout.class).from(tasklist));

        final Class<? extends ServiceEngine> sender = getService(
                new ComponentName(getIntent().getPackage(), getIntent().getAction()));

        service = new ServiceSender(this, sender);
        bound = service.bindService(ServiceEngine.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bound) {
            autorefresh = new Timer();
            autorefresh.schedule(refresh, 2000, 2000);
        }
    }

    @Override
    protected void onPause() {
        if (autorefresh != null) {
            autorefresh.cancel();
            autorefresh = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        service.unbindService();
        super.onDestroy();
    }

    private final TimerTask refresh = new TimerTask() {
        @Override
        public void run() {
            if (service.isConnected()) try {
                tasklist.setData(service.getAllPendingJobs());
                activity.onDataReceived();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Call this method once - on your Application start.
     * Before you can deliver the notification on Android 8.0 and higher, you
     * must register your app's notification channel with the system by passing
     * an instance of NotificationChannel to createNotificationChannel().
     */
    public static void createNotificationChannel(
            @NonNull Context context, @StringRes int id, @StringRes int name)
    {
        createNotificationChannel(context, context.getString(id), context.getText(name));
    }

    public static void createNotificationChannel(
            @NonNull Context context, @NonNull String id, @NonNull CharSequence name)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager systemtray =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            final NotificationChannel channel = new NotificationChannel(id, name,
                    NotificationManager.IMPORTANCE_LOW);

            // channel.setSound(null, null);
            systemtray.createNotificationChannel(channel);
        }
    }

    /**
     * Call this method from your service, in Service.onCreate():
     *     startForeground(notification_id, MainApplication.createNotification(...)
     * Communication channel has to be created before in Application.onCreate():
     *     createNotificationChannel(...)
     * You need permissions in AndroidManifest.xml file:
     *     <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
     */
    public static Notification createNotification(
            Class<? extends ServiceActivity> activity, @NonNull Context context, @StringRes int id,
            @DrawableRes int icon, @StringRes int title, @StringRes int text)
    {
        return createNotification(activity, context, context.getString(id),
                Icon.createWithResource(context, icon), context.getText(title), context.getText(text));
    }

    public static Notification createNotification(
            Class<? extends ServiceActivity> activity, @NonNull Context context, @NonNull String id,
            Icon icon, CharSequence title, CharSequence text)
    {
        final Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, id);
        } else {
            builder = DeprecatedNotificationBuilder(context);
        }

        final Intent intent = new Intent(context, activity);
        intent.setPackage(context.getPackageName()).setAction(context.getClass().getName());

        final PendingIntent pending = PendingIntent.getActivity(context, 0,
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);

        return builder.setContentIntent(pending) // The intent to send when the entry is clicked
                .setWhen(System.currentTimeMillis()) // the time stamp
                .setSmallIcon(icon) // the status icon
                .setContentTitle(title) // the label of the entry
                .setContentText(text) // the contents of the entry
                .build();
    }

    @Deprecated
    private static Notification.Builder DeprecatedNotificationBuilder(Context context) {
        final Notification.Builder builder = new Notification.Builder(context);
        // builder.setSound(null);
        return builder;
    }

    // https://titanwolf.org/Network/Articles/Article?AID=3a5e6098-dfd4-47a9-8313-da431e5ee3bb
    private View getActivityLayout() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    protected @Nullable Class<? extends ServiceEngine> getService(ComponentName component) {
        try {
            return Class.forName(component.getClassName()).asSubclass(ServiceEngine.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class AdapterLayout extends TextView {
        public final TextView textName;

        public AdapterLayout(Context context) {
            super(context);
            textName = this;
        }
    }

    public static class ActivityLayout extends LinearLayout {
        private static final String ru = new Locale("ru").getLanguage();
        private final boolean russian = Locale.getDefault().getLanguage().equals(ru);
        public final TextView labelActions;
        public final ListView listActions;
        public final TextView textProtocol;
        public final Button buttonClear;
        public final Button buttonCancel;

        public ActivityLayout(Context context) {
            super(context);
            this.setOrientation(VERTICAL);

            labelActions = new TextView(context);
            labelActions.setTypeface(null, Typeface.BOLD);
            labelActions.setText(android.R.string.unknownName);
            this.addView(labelActions);

            listActions = new ListView(context);
            listActions.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.7f));
            listActions.invalidateViews();
            this.addView(listActions);

            final TextView labelProtocol = new TextView(context);
            labelProtocol.setTypeface(null, Typeface.BOLD);
            labelProtocol.setText(android.R.string.dialog_alert_title);
            this.addView(labelProtocol);

            final ScrollView scrollProtocol = new ScrollView(context);
            scrollProtocol.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
            this.addView(scrollProtocol);

            textProtocol = new TextView(context);
            scrollProtocol.addView(textProtocol);

            final LinearLayout layoutButtons = new LinearLayout(context);
            layoutButtons.setOrientation(HORIZONTAL);
            this.addView(layoutButtons);

            buttonClear = new Button(context);
            buttonClear.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            buttonClear.setText(russian ? "Очистить" : "Clear");
            layoutButtons.addView(buttonClear);

            buttonCancel = new Button(context);
            buttonCancel.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            buttonCancel.setText(android.R.string.cancel);
            layoutButtons.addView(buttonCancel);

            final Button buttonClose = new Button(context);
            buttonClose.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
            buttonClose.setText(android.R.string.ok);
            buttonClose.setOnClickListener(OnClickClose);
            layoutButtons.addView(buttonClose);
        }

        public void onDataReceived() {
            labelActions.setText(android.R.string.selectTextMode);
        }

        private final OnClickListener OnClickClose = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ServiceActivity)v.getContext()).finish();
            }
        };
    }

    private static class TaskAdapter extends AdapterTemplate<ServiceInterface.JobInfo> {
        public TaskAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final AdapterLayout layout = (AdapterLayout) holder.getView();
            final ServiceInterface.JobInfo item = getItem(position);
            layout.textName.setText(item.process.getClassName());
        }

        @Override
        public ServiceInterface.JobInfo getItem(int position) {
            final TaskList dataset = (TaskList) dataset();
            if (dataset == null) return null;
            dataset.moveToPosition(position);
            return dataset.getValue(0);
        }
    }

    private static class TaskList extends AdapterTemplate.SimpleCursor {
        private List<ServiceInterface.JobInfo> dataset = null;

        public void setData(List<ServiceInterface.JobInfo> dataset) {
            if (this.dataset == null || !this.dataset.equals(dataset)) {
                this.dataset = dataset;
                requery();
            }
        }

        public ServiceInterface.JobInfo getValue(int column) {
            return dataset.get(getPosition());
        }

        @Override
        public int getCount() {
            if (dataset == null) return 0;
            return dataset.size();
        }
    }

    private class ServiceSender extends ServiceConnector {
        public ServiceSender(@NonNull Context client, @NonNull Class<? extends ServiceEngine> server) {
            super(client, server);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            super.onServiceConnected(name, service);
            refresh.run();
        }

        @Override
        public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {
        }
    }
}