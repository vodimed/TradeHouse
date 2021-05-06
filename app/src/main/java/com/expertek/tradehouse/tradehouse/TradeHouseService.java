package com.expertek.tradehouse.tradehouse;

import com.common.extensions.exchange.ServiceActivity;
import com.common.extensions.exchange.ServiceEngine;
import com.expertek.tradehouse.R;

public class TradeHouseService extends ServiceEngine {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
            startForeground(R.string.service_tradehouse, ServiceActivity.createNotification(
                    ServiceActivity.class, this, R.string.CHANNEL_ID, R.drawable.ic_launcher,
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
