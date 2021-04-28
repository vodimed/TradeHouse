package com.expertek.tradehouse.exchange;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.expertek.tradehouse.R;
import com.expertek.tradehouse.tradehouse.TradeHouseService;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_house);
        //eventProcessor.bindService();
    }

    @Override
    protected void onDestroy() {
        //eventProcessor.unbindService();
        super.onDestroy();
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onServiceResult(@NonNull JobInfo work, @Nullable Bundle result) {
            Log.d("RESULT", "onReceiveResult");
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {

        }
    };

    public void onClose(View view) {
        finish();
    }
}