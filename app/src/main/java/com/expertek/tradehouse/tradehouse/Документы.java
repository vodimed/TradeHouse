package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.components.MainSettings;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;

public class Документы extends TradeHouseTask {
    @Override
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "raw");
        final String TradeHouseObjType = MainSettings.TradeHouseObjType.replace("маг", "shop"); // encoding "windows-1251"
        connection.setRequestProperty("user-Agent", String.format(Locale.getDefault(), "%s?%d?True?.", TradeHouseObjType, MainSettings.TradeHouseObjCode));
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
