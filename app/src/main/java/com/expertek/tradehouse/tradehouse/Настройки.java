package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Настройки extends TradeHouseTask {
    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();

        connection.connect();
        request(connection.getOutputStream(), REQ_SETTINGS);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        if ("text/csv".equals(connection.getContentType())) {
            response(connection.getInputStream(), result);
        }
        return result;
    }
}
