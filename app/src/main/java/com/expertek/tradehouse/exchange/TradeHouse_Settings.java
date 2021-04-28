package com.expertek.tradehouse.exchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TradeHouse_Settings extends TradeHouseTask {
    @Override
    public Boolean call() throws Exception {
        if (!cancelled) {
            connection.connect();
            request(connection.getOutputStream(), REQ_SETTINGS);
        }

        if (!cancelled) {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }

            if ("text/csv".equals(connection.getContentType())) {
                response(connection.getInputStream(), result);
            }
        }

        return (!cancelled);
    }
}
