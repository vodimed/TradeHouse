package com.expertek.tradehouse.tradehouse;

import android.os.Bundle;

import com.expertek.tradehouse.documents.entity.document;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

public class Проводка extends TradeHouseTask {
    private document export = null;

    @Override
    public Bundle call() throws Exception {
        final Bundle result = new Bundle();

        export = (document) params.getSerializable(document.class.getName());

        connection.connect();
        request(connection.getOutputStream(), export.DocType.startsWith("Inv") ? REQ_INVENTORY : REQ_WAYBILL);

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(connection.getResponseMessage());
        }

        if ("text/csv".equals(connection.getContentType())) {
            response(connection.getInputStream(), result);
        }
        export.Complete = false;
        result.putSerializable(document.class.getName(), export);
        return result;
    }

    @Override
    protected void request(OutputStream outputStream, XmlSerializer serializer) throws IOException, XmlPullParserException {
        serializer.startTag("", "header");
        serialize(export, serializer);

        serializer.startTag("", "line");
        serializer.endTag("", "line");

        serializer.startTag("", "MarkLines");
        serializer.endTag("", "MarkLines");
        serializer.endTag("", "header");
    }

    private void serialize(Serializable object, XmlSerializer serializer) throws IOException, XmlPullParserException {
        final Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
//            serializer.attribute("", field.getName(), qualifier);
        }
    }
}
/*
Connectivity.cs

        private static string makeDocForSending(DocumentsDataSet.MT_documentsRow docRow)
        {
            XmlDocument xmlDoc = new XmlDocument();
            XmlNode rootNode = Connectivity.makeXMLRootNode(ref xmlDoc);
            Storage.DocTypes docType = Storage.DocTypes.Parse(docRow.DocType);
            rootNode.Attributes["item"].Value = docType.GeneralType == Storage.DocTypesGeneral.Inv ?
                "INVENTORY" : "WAYBILL";

            XmlNode headerNode = xmlDoc.CreateElement("header");
            foreach (DataColumn col in docRow.Table.Columns)
            {
                headerNode.Attributes.Append(xmlDoc.CreateAttribute(col.ColumnName));
                headerNode.Attributes[col.ColumnName].Value = docRow[col].ToString();
            }
            rootNode.AppendChild(headerNode);

            DocumentsDataSet.MT_linesRow[] lines = (DocumentsDataSet.MT_linesRow[])docRow.GetMT_linesRows();
            for (int i = 0; i < lines.Length; i++)
            {
                XmlNode lineNode = xmlDoc.CreateElement("line");
                foreach (DataColumn col in lines[i].Table.Columns)
                {
                    lineNode.Attributes.Append(xmlDoc.CreateAttribute(col.ColumnName));
                    lineNode.Attributes[col.ColumnName].Value = lines[i][col].ToString();
                }
                rootNode.AppendChild(lineNode);
            }

            DocumentsDataSet.MT_MarkLinesRow[] markLinesRows = (DocumentsDataSet.MT_MarkLinesRow[])docRow.GetMT_MarkLinesRows();
            for (int ii = 0; ii < markLinesRows.Length; ii++)
            {
                XmlNode MarkNode = xmlDoc.CreateElement("MarkLines");
                foreach (DataColumn col in markLinesRows[ii].Table.Columns)
                {
                    MarkNode.Attributes.Append(xmlDoc.CreateAttribute(col.ColumnName));
                    MarkNode.Attributes[col.ColumnName].Value = markLinesRows[ii][col].ToString();
                }
                rootNode.AppendChild(MarkNode);
            }

            return xmlDoc.OuterXml;
        }

        private static XmlDocument parseXMLResponse(string resp)
        {
            XmlDocument xmlRcvDoc = new XmlDocument();
            try { xmlRcvDoc.LoadXml(resp); }
            catch (Exception)
            {
                //MessageBox.Show("Ошибка парсинга ответа сервера.");
                Cursor.Current = Cursors.Default;
                return null;
            }

            return xmlRcvDoc;
        }
*/
