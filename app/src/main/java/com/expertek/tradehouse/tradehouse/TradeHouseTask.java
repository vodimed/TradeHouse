package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.components.MainSettings;

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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
    protected int readTimeout = 0;
    protected String getquery = null;
    protected Bundle params = null;
    protected volatile boolean cancelled = false;
    private final List<String> log = new ArrayList<String>(10);

    public TradeHouseTask() {
        MainSettings.reloadPreferences();
    }

    // Override to change
    protected void setRequestHeaders() {
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setDoOutput(true);
    }

    @Override
    public void onCreate(@Nullable Bundle params) throws Exception {
        this.params = params;

        final String address = (MainSettings.Tethering ?
                ConnectionReceiver.getConnectedIp() :
                MainSettings.TradeHouseAddress);

        connection = (HttpURLConnection) new URL(
                "http", address, MainSettings.TradeHousePort,
                (getquery != null ? "?" + getquery : "")).openConnection();
        connection.setConnectTimeout(MainSettings.ConnectionTimeout);
        connection.setReadTimeout(readTimeout);
        setRequestHeaders();
    }

    @Override
    public void onCancel() throws UnsupportedOperationException {
        cancelled = true;
    }

    @Override
    public void onDestroy() throws Exception {
        if (connection != null) connection.disconnect();
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

        try {
            // Start document
            serializer.startDocument(Charset.defaultCharset().name(), true);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            // Open Tag <TSD>
            serializer.startTag("", "TSD");
            serializer.attribute("", "item", qualifier);
            serializer.attribute("", "from", MainSettings.SerialNumber);
            serializer.attribute("", "to", MainSettings.TradeHouseObjType + Math.max(MainSettings.TradeHouseObjCode, 1));
            serializer.attribute("", "user", MainSettings.TradeHouseUserId);
            serializer.attribute("", "tstamp", String.valueOf(System.currentTimeMillis()));
            serializer.attribute("", "version", Application.getVersion());
            serializer.attribute("", "currDecSeparator", String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator()));
            serializer.attribute("", "shortDatePattern", ((SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)).toPattern());
            serializer.attribute("", "longTimePattern", ((SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.LONG)).toPattern());
            serializer.attribute("", "MarksReg", String.valueOf(MainSettings.CheckMarks));

            request(serializer);

            // End tag <TSD>
            serializer.endTag("", "TSD");

            // End document
            serializer.endDocument();
        } finally {
            serializer.flush();
            writer.flush();
            writer.close();
            outputStream.flush();
            outputStream.close();
        }
    }

    protected boolean binary_request(OutputStream outputStream, File resource) throws IOException {
        FileInputStream inputStream;
        int totalWrite = 0;
        try {
            inputStream = new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            inputStream = null;
        }

        final byte[] buffer = new byte[1024];
        try {
            for (int bytesRead; !cancelled && (inputStream != null) && ((bytesRead = inputStream.read(buffer)) != -1);) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                totalWrite += bytesRead;
                setProgress(String.format(Locale.getDefault(), "%dKb written", totalWrite / 1024));
            }
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return (!cancelled);
    }

    protected void response(InputStream inputStream, Bundle content) throws IOException, XmlPullParserException {
        final XmlPullParser parser = xmlfactory.newPullParser();
        parser.setInput(inputStream, charset.name());
        String thistag = null;

        try {
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
        } finally {
            inputStream.close();
        }
    }

    protected boolean binary_response(InputStream inputStream, File resource) throws IOException {
        final File temporaryFile = temporary(resource);
        final FileOutputStream outputStream = new FileOutputStream(temporaryFile, false);
        int totalRead = 0;

        final byte[] buffer = new byte[1024];
        try {
            for (int bytesRead; !cancelled && (inputStream != null) && ((bytesRead = inputStream.read(buffer)) != -1);) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
                totalRead += bytesRead;
                setProgress(String.format(Locale.getDefault(), "%dKb read", totalRead / 1024));
            }
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return (!cancelled);
    }

    public static File temporary(File original) {
        return new File(original.getPath() + "_");
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
