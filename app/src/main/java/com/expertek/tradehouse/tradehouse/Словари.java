package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class Словари extends TradeHouseTask {
    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();
        final File dictionaries = MainApplication.app().getDatabasePath(MainSettings.Dictionaries_db);

        connection.connect();
        request(connection.getOutputStream(), REQ_DICTIONARIES);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        if ("text/csv".equals(connection.getContentType())) {
            response(connection.getInputStream(), result);
        } else if (binary_response(connection.getInputStream(), dictionaries)) {
            result.putInt(dictionaries.getName(), 1);
        }
        return result;
    }
}
