package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.components.MainSettings;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Настройки extends TradeHouseTask {
    public Настройки() {
        super();
        this.readTimeout = MainSettings.CheckTimeout;
    }

    @Override
    protected void request(XmlSerializer serializer) throws IOException, XmlPullParserException {
        serializer.startTag("", "SN");
        serializer.attribute("", "type", "HARDWARE");
        serializer.attribute("", "value", MainSettings.SerialNumber);
        serializer.endTag("", "SN");

        serializer.startTag("", "OBJ");
        serializer.attribute("", "obj_type", MainSettings.TradeHouseObjType);
        serializer.attribute("", "obj_code", String.valueOf(Math.max(MainSettings.TradeHouseObjCode, 1)));
        serializer.endTag("", "OBJ");
    }

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
