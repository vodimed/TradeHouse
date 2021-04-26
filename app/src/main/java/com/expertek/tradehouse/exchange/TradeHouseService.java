package com.expertek.tradehouse.exchange;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.R;

public class TradeHouseService extends ServiceEngine {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            /* <uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> */
            startForeground(R.string.service_tradehouse, MainApplication.createNotification(
                    TradeHouseActivity.class,
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
