package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

public class Документы extends TradeHouseTask {
    @Override
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "raw");
        final String[] magheader = parseMagHeader(MainSettings.TradeHouseObject);
        magheader[0].replace("маг", "shop"); // encoding "windows-1251"
        connection.setRequestProperty("user-Agent", String.format("%s?%s?True?.", magheader[0], magheader[1]));
        connection.setDoOutput(true);
    }

    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();
        final File documents = Application.app().getDatabasePath(MainSettings.Documents_db);

        connection.connect();
        cancelled = !binary_request(connection.getOutputStream(), documents);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        // By 12.10.2021 Server sends incorrect ContentType="text/csv" for "raw" data
        if ("text/csv".equals(connection.getContentType()) && connection.getContentLength() < 8192) {
            response(connection.getInputStream(), result);
        } else if (binary_response(connection.getInputStream(), documents)) {
            result.putSerializable(documents.getName(), Application.documents.version());
        }
        return result;
    }
}
