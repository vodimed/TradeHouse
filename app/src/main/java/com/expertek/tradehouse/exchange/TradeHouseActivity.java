package com.expertek.tradehouse.exchange;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.expertek.tradehouse.R;

public class TradeHouseActivity extends Activity {
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

    private final ServiceLink tradehouse = new ServiceLink(this, TradeHouseService.class) {
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