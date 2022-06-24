package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.components.MainSettings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Документы extends TradeHouseTask {
    public Документы() {
        super();
        this.readTimeout = MainSettings.SendTimeout;
    }

    @Override
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "raw");
        final String TradeHouseObjType = MainSettings.TradeHouseObjType.replace("маг", "shop"); // encoding "windows-1251"
        connection.setRequestProperty("user-Agent", String.format(Locale.getDefault(), "%s?%d?%b?%c?%b",
                TradeHouseObjType, MainSettings.TradeHouseObjCode, MainSettings.CheckMarks,
                DecimalFormatSymbols.getInstance().getDecimalSeparator(), MainSettings.CheckMarks));
        connection.setDoOutput(true);
    }

    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();
        final File documents = Application.app().getDatabasePath(MainSettings.Documents_db);

        connection.connect();
        binary_request(connection.getOutputStream(), documents);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        // Skip Bad-Formed Header messages
        if (connection.getHeaderField("ETag") != null) {
            final InputStream skip = connection.getInputStream();
            int counter = 0;

            for (int code = 0, prev = 0; (counter < 2) && (code >= 0); prev = code) {
                code = skip.read();

                if ((prev == 0xD) && (code == 0xA)) {
                    counter++;
                } else if ((prev != 0xA) || (code != 0xD)) {
                    counter = 0;
                }
            }
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
