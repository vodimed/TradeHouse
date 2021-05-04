package com.expertek.tradehouse.tradehouse;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.R;
import com.common.extensions.exchange.ServiceEngine;
import com.common.extensions.exchange.ServiceActivity;

public class TradeHouseService extends ServiceEngine {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            /* <uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> */
            startForeground(R.string.service_tradehouse, MainApplication.createNotification(
                    ServiceActivity.class,
                    R.string.service_tradehouse,
                    R.string.msgTradeHouseServiceNotification));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            stopForeground(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
