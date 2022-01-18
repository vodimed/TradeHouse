package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.extensions.Logger;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.MainSettings;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class TradeHouseTask implements ServiceInterface.ServiceTask {
    protected static final String REQ_SETTINGS = "SETTINGS";
    protected static final String REQ_DICTIONARIES = "TH_ALL";
    protected static final String REQ_INVENTORY = "INVENTORY";
    protected static final String REQ_WAYBILL = "WAYBILL";

    protected static final Charset charset = Charset.forName("windows-1251"); // cp1251
    protected static final XmlPullParserFactory xmlfactory = createXmlFactory();
    protected HttpURLConnection connection;
    protected String getquery = null;
    protected Bundle params = null;
    protected volatile boolean cancelled = false;
    private final List<String> log = new ArrayList<String>(10);

    // Override to change
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setDoOutput(true);
    }

    @Override
    public void onCreate(@Nullable Bundle params) throws Exception {
        this.params = params;
        MainSettings.reloadPreferences();

        final String address = (MainSettings.Tethering ?
                ConnectionReceiver.getConnectedIp() :
                MainSettings.TradeHouseAddress);

        connection = (HttpURLConnection) new URL(
                "http", address, MainSettings.TradeHousePort,
                (getquery != null ? "?" + getquery : "")).openConnection();
        connection.setConnectTimeout(MainSettings.ConnectionTimeout);
        setRequestHeaders();
    }

    @Override
    public void onCancel() throws UnsupportedOperationException {
        cancelled = true;
    }

    @Override
    public void onDestroy() throws Exception {
        connection.disconnect();
    }

    @Override
    public void setProgress(String progress) {
        log.add(progress);
    }

    @Override
    public List<String> getProgress(int since) {
        return log.subList(since, log.size());
    }

    private static XmlPullParserFactory createXmlFactory() {
        try {
            return XmlPullParserFactory.newInstance();
        } catch (XmlPullParserException e) {
            Logger.e(e);
            return null;
        }
    }

    protected void request(XmlSerializer serializer) throws IOException, XmlPullParserException {
        // Override to place request body
    }

    protected void request(OutputStream outputStream, String qualifier) throws IOException, XmlPullParserException {
        final OutputStreamWriter writer = new WindowsStreamWriter(outputStream, charset);
        final XmlSerializer serializer = xmlfactory.newSerializer();
        serializer.setOutput(writer);

        // Start document
        serializer.startDocument(Charset.defaultCharset().name(), true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        final String[] magheader = parseMagHeader(MainSettings.TradeHouseObject);

        // Open Tag <TSD>
        serializer.startTag("", "TSD");
        serializer.attribute("", "item", qualifier);
        serializer.attribute("", "from", MainSettings.SerialNumber);
        serializer.attribute("", "to", MainSettings.TradeHouseObject);
        serializer.attribute("", "user", "1-1");
        serializer.attribute("", "tstamp", String.valueOf(System.currentTimeMillis()));
        serializer.attribute("", "version", "2.4b");
        serializer.attribute("", "currDecSeparator", ".");
        serializer.attribute("", "shortDatePattern", "M/d/yy");
        serializer.attribute("", "longTimePattern", "h:mm:ss tt");
        serializer.attribute("", "MarksReg", "True");
        serializer.attribute("", "user-agent", String.format("%s?%s?True?,?True", magheader[0], magheader[1]));

        if (REQ_SETTINGS.equals(qualifier)) {
            serializer.startTag("", "SN");
            serializer.attribute("", "type", "HARDWARE");
            serializer.attribute("", "value", MainSettings.SerialNumber);
            serializer.endTag("", "SN");

            serializer.startTag("", "OBJ");
            serializer.attribute("", "obj_type", magheader[0]);
            serializer.attribute("", "obj_code", magheader[1]);
            serializer.endTag("", "OBJ");
        }

        // End tag <TSD>
        serializer.endTag("", "TSD");

        request(serializer);

        // End document
        serializer.endDocument();

        serializer.flush();
        writer.flush();
        writer.close();
        outputStream.flush();
        outputStream.close();
    }

    protected boolean binary_request(OutputStream outputStream, File resource) throws IOException {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            inputStream = null;
        }

        if (inputStream != null) {
            final byte[] buffer = new byte[1024];
            int totalWrite = 0;
            for (int bytesRead; !cancelled && ((bytesRead = inputStream.read(buffer)) != -1);) {
                outputStream.write(buffer, 0, bytesRead);
                totalWrite += bytesRead;
                setProgress(String.format(Locale.getDefault(), "%dKb written", totalWrite / 1024));
            }
            inputStream.close();
        }

        outputStream.flush();
        outputStream.close();
        return (!cancelled);
    }

    protected void response(InputStream inputStream, Bundle content) throws IOException, XmlPullParserException {
        final XmlPullParser parser = xmlfactory.newPullParser();
        parser.setInput(inputStream, charset.name());
        String thistag = null;

        for (int eventType = parser.getEventType(); !cancelled &&
                (eventType != XmlPullParser.END_DOCUMENT); eventType = parser.next())
        {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    thistag = parser.getName();
                    for (int i = 0, size = parser.getAttributeCount(); i < size; i++) {
                        content.putString(parser.getAttributeName(i), parser.getAttributeValue(i));
                    }
                    break;
                case XmlPullParser.TEXT:
                    content.putString(thistag, parser.getText());
                    break;
            }
        }
        inputStream.close();
    }

    protected boolean binary_response(InputStream inputStream, File resource) throws IOException {
        final File temporaryFile = temporary(resource);
        final FileOutputStream outputStream = new FileOutputStream(temporaryFile, false);

        final byte[] buffer = new byte[1024];
        int totalRead = 0;
        for (int bytesRead; !cancelled && ((bytesRead = inputStream.read(buffer)) != -1);) {
            outputStream.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            setProgress(String.format(Locale.getDefault(), "%dKb read", totalRead / 1024));
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();

        return (!cancelled);
    }

    public static File temporary(File original) {
        return new File(original.getPath() + "_");
    }

    protected static String[] parseMagHeader(String thObject) {
        final String[] result = new String[2];
        for (int i = 0; i < thObject.length(); i++) {
            if (thObject.charAt(i) <= '9') {
                result[0] = thObject.substring(0, i);
                result[1] = thObject.substring(i);
                return result;
            }
        }
        result[0] = thObject;
        result[1] = "";
        return result;
    }

    // Correct Java error with encoding marker
    private static class WindowsStreamWriter extends OutputStreamWriter {
        private final static char[] replacement = String.format(
                "<?xml version='1.0' encoding='%s'", TradeHouseTask.charset.name()).toCharArray();

        public WindowsStreamWriter(OutputStream out, Charset cs) {
            super(out, cs);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if ((cbuf[off] == '<') && (cbuf[off + 1] == '?')) {
                for (int i = 0; i < replacement.length; i++) {
                    cbuf[off + i] = replacement[i];
                }
                for (int i = replacement.length; i < off + len; i++) {
                    if (cbuf[i] == '?') break;
                    cbuf[i] = ' ';
                }
            }
            super.write(cbuf, off, len);
        }
    }
}
