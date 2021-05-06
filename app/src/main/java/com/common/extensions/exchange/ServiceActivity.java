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
import android.util.Log;
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
import com.expertek.tradehouse.tradehouse.TradeHouseService;

import java.util.Locale;

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
    private ActivityLayout activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ActivityLayout(this));
        //setContentView(R.layout.service_activity);

        activity = (ActivityLayout) this.getActivityLayout();
        activity.listActions.setAdapter(new RAdapter(this, AdapterLayout.class));

        ComponentName c = getCallingActivity();
        Intent intent = getIntent();

        //boolean res = processor.bindService(0);
        //res = res;
    }

    @Override
    protected void onDestroy() {
        processor.unbindService();
        super.onDestroy();
    }

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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setPackage(context.getClass().getName()); //TODO

        final PendingIntent pending = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public static class AdapterLayout extends TextView {
        public AdapterLayout(Context context) {
            super(context);
        }
    }

    public static class ActivityLayout extends LinearLayout {
        private static final String ru = new Locale("ru").getLanguage();
        private final boolean russian = Locale.getDefault().getLanguage().equals(ru);
        public final ListView listActions;
        public final TextView textProtocol;
        public final Button buttonClear;
        public final Button buttonCancel;

        public ActivityLayout(Context context) {
            super(context);
            this.setOrientation(VERTICAL);

            final TextView labelActions = new TextView(context);
            labelActions.setTypeface(null, Typeface.BOLD);
            labelActions.setText(android.R.string.selectTextMode);
            this.addView(labelActions);

            listActions = new ListView(context);
            listActions.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 3.0f));
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

        private final OnClickListener OnClickClose = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ServiceActivity)v.getContext()).finish();
            }
        };
    }

    private static class RAdapter extends AdapterTemplate<Long> {
        public RAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return (ViewHolder) super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AdapterLayout layout = (AdapterLayout) holder.getView();
            layout.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public Long getItem(int position) {
            return null;
        }
    }

    private final ServiceConnector processor = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
            Log.d("RESULT", "onReceiveResult");
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {

        }
    };
}