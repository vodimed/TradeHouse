package com.expertek.tradehouse.tradehouse;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class ThDictionaries extends TradeHouseTask {
    @Override
    public Boolean call() throws Exception {
        final File dictionaries = MainApplication.app().getDatabasePath(MainSettings.Dictionaries_db);

        if (!cancelled) {
            connection.connect();
            request(connection.getOutputStream(), REQ_DICTIONARIES);
        }

        if (!cancelled) {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage());
            }

            if ("text/csv".equals(connection.getContentType())) {
                response(connection.getInputStream(), result);
            } else if (binary_response(connection.getInputStream(), dictionaries)) {
                result.putInt(dictionaries.getName(), 1);
            }
        }

        return (!cancelled);
    }
}
