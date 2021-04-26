package com.expertek.tradehouse.exchange;

import com.expertek.tradehouse.MainSettings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class TradeHouse_Query extends TradeHouseTask {
    @Override
    public Boolean call() throws Exception {
        String string = "<TSD item=\"SETTINGS\" from=\"G14G88755\" to=\"маг1\" " +
                "user=\"1-1\" tstamp=\"1270325113\" version=\"2.4b\" currDecSeparator=\".\" " +
                "shortDatePattern=\"M/d/yy\" longTimePattern=\"h:mm:ss tt\" " +
                "MarksReg=\"True\" user-agent=\"маг?1?True?\">\n" +
                "<SN type=\"HARDWARE\" value=\"G14G88755\"/>\n" +
                "<OBJ obj_type=\"маг\" obj_code=\"1\"/>\n</TSD>";

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(MainSettings.ConnectionTimeout);
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(
                connection.getOutputStream(), Charset.forName("cp1251"));

        writer.write(string);
        writer.flush();
        writer.close();

        if (connection.getResponseCode() != 200) {
            System.err.println("connection failed");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), Charset.forName("cp1251")));

        while (!cancelled) {
            final String output = reader.readLine();
            if (output == null) break; // EOF
        }

        return false;
    }
}
