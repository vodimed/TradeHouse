package com.expertek.tradehouse.exchange;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class TradeHouse_Documents extends TradeHouseTask {
    @Override
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "raw");
        connection.setRequestProperty("User-Agent", "маг?202?True?.");
        connection.setDoOutput(true);
    }

    @Override
    public Boolean call() throws Exception {
        final File documents = MainApplication.inst().getDatabasePath(MainSettings.DocumentsDB);

        if (!cancelled) {
            connection.connect();
            cancelled = !binary_request(connection.getOutputStream(), documents);
        }

        if (!cancelled) {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }

            if ("text/csv".equals(connection.getContentType())) {
                response(connection.getInputStream(), result);
            } else if (binary_response(connection.getInputStream(), documents)) {
                result.putInt(documents.getName(), 1);
            }
        }

        return (!cancelled);
    }
}
