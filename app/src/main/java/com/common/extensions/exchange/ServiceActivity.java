package com.common.extensions.exchange;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.AdapterTemplate;

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

        activity = (ActivityLayout) getActivityLayout();

        activity.listActions.setAdapter(new RAdapter(this, AdapterLayout.class));

        //eventProcessor.bindService();
    }

    // https://titanwolf.org/Network/Articles/Article?AID=3a5e6098-dfd4-47a9-8313-da431e5ee3bb
    private View getActivityLayout() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    public static class ActivityLayout extends LinearLayout {
        public final ListView listActions;

        public ActivityLayout(Context context) {
            super(context);
            this.setOrientation(HORIZONTAL);

            final TextView textActions = new TextView(context);
            textActions.setTypeface(null, Typeface.BOLD);
            this.addView(textActions);

            listActions = new ListView(context);
            listActions.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2.0f));
            this.addView(listActions);

            final TextView textProtocol = new TextView(context);
            textProtocol.setTypeface(null, Typeface.BOLD);
            this.addView(textProtocol);
        }
    }

    public static class AdapterLayout extends TextView {
        public AdapterLayout(Context context) {
            super(context);
        }
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
            //Layout layout = (Layout) holder.getView();
            //((TextView) layout.getChildAt(0)).setText(String.valueOf(position));
            //((TextView) layout.getChildAt(1)).setText(String.valueOf(position));
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

    @Override
    protected void onDestroy() {
        //eventProcessor.unbindService();
        super.onDestroy();
    }

    /*
    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
            Log.d("RESULT", "onReceiveResult");
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {

        }
    };
    */

    public void onClose(View view) {
        finish();
    }
}