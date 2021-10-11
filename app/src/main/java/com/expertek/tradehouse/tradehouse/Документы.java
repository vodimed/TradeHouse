package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class Документы extends TradeHouseTask {
    @Override
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "raw");
        //connection.setRequestProperty("user-Agent", "маг?202?True?."); //TODO: encoding
        connection.setRequestProperty("user-Agent", "shop?202?True?.");
        connection.setDoOutput(true);
    }

    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();
        final File documents = MainApplication.app().getDatabasePath(MainSettings.Documents_db);

        connection.connect();
        cancelled = !binary_request(connection.getOutputStream(), documents);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        //TODO: remove false
        if (false && "text/csv".equals(connection.getContentType())) {
            response(connection.getInputStream(), result);
        } else if (binary_response(connection.getInputStream(), documents)) {
            result.putSerializable(documents.getName(), MainApplication.documents.version());
        }
        return result;
    }
}
