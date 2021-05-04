package com.common.extensions.exchange;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.common.extensions.AdapterRecycler;
import com.expertek.tradehouse.R;

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
    private ListView listActions = null;
    private RecyclerView viewActions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_activity);

        listActions = findViewById(R.id.listActions);
        viewActions = findViewById(R.id.viewActions);
        viewActions.setLayoutManager(new LinearLayoutManager(this));

        //listActions.setAdapter(new RAdapter(this, R.layout.main_activity));
        //viewActions.setAdapter(new RAdapter(this, R.layout.main_activity));

        //eventProcessor.bindService();
    }

    private Class<View> createLayout;

    // AdapterTemplate
    private static class RAdapter extends AdapterRecycler<Long> {
        public RAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
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