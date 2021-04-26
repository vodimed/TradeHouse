package com.expertek.tradehouse.exchange;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.expertek.tradehouse.MainSettings;

import java.net.HttpURLConnection;
import java.net.URL;

public abstract class TradeHouseTask implements ServiceInterface.Task {
    protected HttpURLConnection connection;
    protected Bundle params = null;
    protected Bundle result = null;
    protected volatile boolean cancelled = false;

    @Override
    public void onCreate(@Nullable Bundle params, @Nullable Bundle result) throws Exception {
        this.params = params;
        this.result = result;

        connection = (HttpURLConnection)new URL(
                "http", MainSettings.ThreadHouseAddress, MainSettings.ThreadHousePort,
                "").openConnection();
    }

    @Override
    public void onDestroy() throws Exception {
        connection.disconnect();
    }

    @Override
    public boolean onCancel() {
        cancelled = true;
        return true;
    }
}
