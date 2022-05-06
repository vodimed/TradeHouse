package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.common.extensions.database.Formatter;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

public class Проводка extends TradeHouseTask {
    private final DBDocuments dbd = Application.documents.db();
    private Document export = null;

    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();

        export = (Document) params.getSerializable(Document.class.getName());

        connection.connect();
        request(connection.getOutputStream(), export.DocType.startsWith("Inv") ? REQ_INVENTORY : REQ_WAYBILL);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        if ("text/csv".equals(connection.getContentType())) {
            response(connection.getInputStream(), result);
        }
        export.Complete = false;
        result.putSerializable(Document.class.getName(), export);
        return result;
    }

    @Override
    protected void request(XmlSerializer serializer) throws IOException, XmlPullParserException {
        serializer.startTag("", "header");
        serialize(export, serializer);
        serializer.endTag("", "header");

        final PagingList<Line> lines = new PagingList<Line>(dbd.lines().load(export.DocName));
        for (Line line : lines) {
            serializer.startTag("", "line");
            serialize(line, serializer);
            serializer.endTag("", "line");
        }

        final PagingList<Markline> marklines = new PagingList<Markline>(dbd.marklines().load(export.DocName, null));
        for (Markline markline : marklines) {
            serializer.startTag("", "MarkLines");
            serialize(markline, serializer);
            serializer.endTag("", "MarkLines");
        }
    }

    private void serialize(Serializable object, XmlSerializer serializer) throws IOException, XmlPullParserException {
        final Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) continue;
            serializer.attribute("", field.getName(), getValue(object, field));
        }
    }

    private String getValue(Serializable object, Field field) {
        final Class<?> type = field.getType();
        try {
            if (type.equals(String.class)) {
                return (String) field.get(object);
            } else if (type.equals(java.util.Date.class)) {
                return Formatter.Date.format((java.util.Date) field.get(object));
            } else if (type.equals(Double.TYPE)) {
                return Formatter.Number.format(field.getDouble(object));
            } else if (type.equals(Float.TYPE)) {
                return Formatter.Number.format(field.getFloat(object));
            } else if (type.equals(Long.TYPE)) {
                return String.valueOf(field.getLong(object));
            } else if (type.equals(Integer.TYPE)) {
                return String.valueOf(field.getInt(object));
            } else if (type.equals(Short.TYPE)) {
                return String.valueOf(field.getShort(object));
            } else if (type.equals(Character.TYPE)) {
                return String.valueOf(field.getChar(object));
            } else if (type.equals(Byte.TYPE)) {
                return String.valueOf(field.getByte(object));
            } else if (type.equals(Boolean.TYPE)) {
                return (field.getBoolean(object) ? "1" : "0");
            }
        } catch (IllegalAccessException e) {
            return null;
        }
        return null;
    }
}