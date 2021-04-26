package com.expertek.tradehouse.exchange;

import android.util.Log;

public class TradeHouse_Sales extends TradeHouseTask {
    static int i = 0;

    @Override
    public Boolean call() throws Exception {
        ++i;
        if (i % 100 == 0)
            Log.d(TradeHouseService.class.getSimpleName(), "" + i);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //host

        return true;
    }
}
