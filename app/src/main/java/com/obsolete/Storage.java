package com.obsolete;using System;
        using System.Windows.Forms;
        using System.Data;
        using System.Data.SQLite;
        using System.Xml;
        using System.IO;
        using System.Linq;
        using System.Collections.Generic;
        using System.Text;
        using System.Reflection;
        using System.Diagnostics;

        using MT.DocumentsDataSetTableAdapters;
        using MT.DictionariesDataSetTableAdapters;

        namespace MT
        {
/// <summary>
/// Класс внутреннего хранилища приложения на мобильном устройстве. Содержит методы для работы с хранилищем.
/// </summary>
public class Storage
{
    public event ActionEventHandler ActionStarting;
    public event ActionEventHandler ActionFinished;


        #region paths and filenames
    private string dbPathDct { get { return Path.Combine(Program.DataDir, "dictionaries.s3db"); } }
    private string dbPathDoc { get { return Path.Combine(Program.DataDir, "documents.s3db"); } }
    public string ConStrDct { get { return "data source = " + dbPathDct + "; UTF8Encoding=True;"; } }
    public string ConStrDoc { get { return "data source = " + dbPathDoc + "; UTF8Encoding=True;"; } }
        #endregion

    public DictionariesDataSet dsDct = new DictionariesDataSet();
    public DocumentsDataSet dsDoc = new DocumentsDataSet();

    public enum TableTypesGeneral { TH, MT }
    public sealed class TableTypes // Enum-like string storage
{
    // Properties storing data
    public TableTypesGeneral Type { get; private set; } // общий тип таблицы (справочник/документы)

    // basic representation of enum (for type conversions)
    private string _enumStr;
    // type converters
    public override string ToString() { return _enumStr; }
    public static implicit operator string(TableTypes table) { return table._enumStr; }

    // private ctor
    private TableTypes(TableTypesGeneral t, string es) { Type = t; _enumStr = es; }

    // enum-like public members storing string constants
    public static readonly TableTypes TH_users     = new TableTypes(TableTypesGeneral.TH, "TH_users");
    public static readonly TableTypes TH_objects   = new TableTypes(TableTypesGeneral.TH, "TH_objects");
    public static readonly TableTypes TH_clients   = new TableTypes(TableTypesGeneral.TH, "TH_clients");
    public static readonly TableTypes TH_barcodes  = new TableTypes(TableTypesGeneral.TH, "TH_barcodes");
    public static readonly TableTypes TH_goods     = new TableTypes(TableTypesGeneral.TH, "TH_goods");
    public static readonly TableTypes MT_documents = new TableTypes(TableTypesGeneral.MT, "MT_documents");
    public static readonly TableTypes MT_lines     = new TableTypes(TableTypesGeneral.MT, "MT_lines");
    public static readonly TableTypes MT_MarkLines = new TableTypes(TableTypesGeneral.MT, "MT_MarkLines");
    public static readonly TableTypes MT_MarkLines_indx = new TableTypes(TableTypesGeneral.MT, "MT_MarkLines_indx");

    // parse function
    public static TableTypes Parse(string s) { return GetAllValues().FirstOrDefault(elem => elem._enumStr == s); }
    // implement GetValues() for enumerations
    public static IEnumerable<TableTypes> GetAllValues() // TODO: maybe use reflection instead?
    {
        return new[] { TH_users, TH_objects, TH_clients, TH_barcodes, TH_goods, MT_documents, MT_lines, MT_MarkLines };
    }

}
    public static IEnumerable<TableTypes> tableTypes = TableTypes.GetAllValues();


    public enum DocFlags
    {
        isDocMarkOn = 0x1
    }

    public enum DocLineFlags
    {
        isEditManualFullQnty = 1,
        isNotEditManualQnty = 0x2
    }

    public enum DocTypesGeneral { Inv, WB }
    public sealed class DocTypes // Enum-like string storage
{
    // Properties storing string data
    public string Mark { get; private set; } // краткое (несколько букв для отображения в таблице документов)
    public string Full { get; private set; } // полное (развернутое описание документа)
    public DocTypesGeneral GeneralType { get; private set; } // общий тип документа (Инв/Накл)

    // string representation of enum (for type conversions)
    private string _enumStr;
    // type converters
    public override string ToString() { return _enumStr; }
    public static implicit operator string(DocTypes docType) { return docType._enumStr; }

    // private ctor
    private DocTypes(string m, string f, DocTypesGeneral t, string e) { Mark = m; Full = f; GeneralType = t; _enumStr = e; }

    // enum-like public members storing string constants
    public static readonly DocTypes Inv        = new DocTypes("и", "Инвентаризация",                 DocTypesGeneral.Inv, "Inv");
    public static readonly DocTypes IntPurchWB = new DocTypes("п", "Накладная внутренняя приходная", DocTypesGeneral.WB,  "IntPurchWB");
    public static readonly DocTypes IntSalesWB = new DocTypes("р", "Накладная внутренняя расходная", DocTypesGeneral.WB,  "IntSalesWB");
    public static readonly DocTypes ExtPurchWB = new DocTypes("П", "Накладная внешняя приходная",    DocTypesGeneral.WB,  "ExtPurchWB");
    public static readonly DocTypes ExtSalesWB = new DocTypes("Р", "Накладная внешняя расходная",    DocTypesGeneral.WB,  "ExtSalesWB");
    public static readonly DocTypes InvMarks   = new DocTypes("И", "Инвентаризация с маркировкой",   DocTypesGeneral.Inv, "InvMarks");
    public static readonly DocTypes InvIntro   = new DocTypes("i", "Инвентар. первоначальный ввод",  DocTypesGeneral.Inv, "InvIntroduce");
    public static readonly DocTypes Introduce  = new DocTypes("I", "Первоначальный ввод",            DocTypesGeneral.WB,  "Introduce");
    public static readonly DocTypes UTD        = new DocTypes("U", "Приход УТД",                     DocTypesGeneral.WB,  "UTD");

    // parse function
    public static DocTypes Parse(string s) { return GetAllValues().FirstOrDefault(elem => elem._enumStr == s); }
    // implement GetValues() for enumerations
    public static IEnumerable<DocTypes> GetAllValues() // TODO: maybe use reflection instead?
    {
        return new[] { Inv, IntPurchWB, IntSalesWB, ExtPurchWB, ExtSalesWB, InvMarks, InvIntro, Introduce, UTD };
    }
}
    public static IEnumerable<DocTypes> docTypesDescriptions = DocTypes.GetAllValues();


    public static bool IsSalesWB(Storage.DocTypes docType)
    {
        return docType == Storage.DocTypes.ExtSalesWB ||
                docType == Storage.DocTypes.IntSalesWB;
    }
    public static bool IsTHDoc(DocumentsDataSet.MT_documentsRow doc)
    {
        return
                doc.DocType == DocTypes.Inv || // Inv is only TH
                        doc.Status == "НАКЛ+" || // or TH waybills
                        doc.Status == "РАЗР+" ||
                        doc.Status == "накл+" || // or TH waybills
                        doc.Status == "накл-" ||
                        doc.Status == "разр+"
                ;
    }

    public static string GetCodeIdent(string iDm)
    {

        string Velement = "first";
        string oCodeIdent = "";

        if (iDm.Length == 29
                && iDm.StartsWith("01") == false
                && iDm.StartsWith("02") == false )
        {
            oCodeIdent = iDm.Substring(0, 21);
        }
        else
        {
            if (
                    (iDm.Length == 25
                            || iDm.Length == 29
                            || iDm.Length == 21)
                            &&
                            (
                                    (!iDm.StartsWith("01")
                                            && !iDm.StartsWith("02"))
                                            || iDm.Substring(16,2) != "21")

            )
            {
                oCodeIdent = iDm.Substring(0, 21);

            }
            else
            {
                if (getGtinByDM(iDm) == "")
                {
                    oCodeIdent = iDm.Substring(0, 21);
                }
                else
                {
                    while (Velement != "" && iDm != "")
                    {
                        Velement = GetNextElement(ref iDm);
                        oCodeIdent = oCodeIdent + Velement;

                    }
                }
            }
        }
        return oCodeIdent;

    }
    public static string GetNextElement(ref string pstr)
    {
        string vlistElem = "00,01,02,21,17,11,13,(01),(02),(21),(17),(11),(13)"; /* ,(8005),8005";*/
        string vlistallleng = "00,00,00,00,00,00,00,0000,0000,0000,0000,0000,0000";  /* ,000000,0000"; */
        string vlistleng1 = "27,14,14,07,06,06,06,0014,0014,0007,0006,0006,0006"; /* ,000006,0006"; */
        string vlistleng2 = "27,14,14,13,06,06,06,0014,0014,0007,0006,0006,0006"; /* ,000006,0006";*/
        string vTeg = "";
        int vLength = 0;
        int vi = 0;
        //int vj = 0;
        bool vErr = false;

        string[] vlistElemArr;
        string[] vlistalllengArr;
        string[] vlistleng1Arr;
        string[] vlistleng2Arr;

        vlistElemArr = vlistElem.Split(',');
        vlistalllengArr = vlistallleng.Split(',');
        vlistleng1Arr = vlistleng1.Split(',');
        vlistleng2Arr = vlistleng2.Split(',');

        if (pstr.Length == 4)
        {
            return "";
        }
        else
        {
            for (vi = 0; vi < vlistElemArr.Length; vi++)
            {
                vTeg = vlistElemArr[vi];
                if (pstr.StartsWith(vlistElemArr[vi]))
                {
                    try { vLength = Int32.Parse(vlistalllengArr[vi]); }
                    catch { vErr = true; }
                    if (vLength == 0 && !vErr)
                    {
                        vLength = Int32.Parse(vlistleng1Arr[vi]);
                    }
                    {
                        vErr = false;
                        //for (vi = 0; vi < vlistElemArr.Length; vi++)
                        //{
                        //vLength = Int32.Parse(entry(vj,entry(vi,vlistallleng),"|")).
                        //if vLength eq length(pstr)
                        //then do:
                        //   vLength = vj.
                        //   leave block-mas.
                        //end.
                        //else
                        //   vLength = ?.
                        //}
                        //vLength = int(entry(vi,if vLength ne ? then vlistleng1 else vlistleng2)).
                        //vLength = Int32.Parse(vlistleng1Arr[vi]);
                    }
                    try { vTeg = pstr.Substring(0, vTeg.Length + vLength); }
                    catch {vTeg = pstr;}
                    try {pstr = pstr.Substring(vTeg.Length);}
                    catch { }
                        goto block_elem;
                }
                else
                {
                    vTeg = "";
                }

            }

        }
        block_elem:
        return vTeg;
    }

    public static string getGtinByDM(string IDM)
    {
        string vtxt;
        string vGtin;
        vtxt = IDM;
        vGtin = IDM;
        if (vtxt.Length > 14)
        {
            if (vtxt.StartsWith("(01)") || vtxt.StartsWith("(02)"))
            {
                vGtin = vtxt.Substring(4, 14);
            }
            else
            {
                if ((vtxt.StartsWith("01") || vtxt.StartsWith("02"))
                        && IDM.Substring(16, 2) == "21"
                        && vtxt.Length >= 25
                )
                {
                    vGtin = vtxt.Substring(2, 14);
                }
                else
                {
                    if (vtxt.Length == 14 + 7 + 4 + 4 || vtxt.Length == 14 + 7 + 4 || vtxt.Length == 14 + 7)
                    {
                        vGtin = vtxt.Substring(0, 14);
                    }
                }
            }
        }
        return vGtin;
    }
    /// <summary>
    /// Конструктор создает объект хранилища - связку существующих файлов базы данных устройства и их представления в памяти.
    /// </summary>
    public Storage()
    {
        // checking for storage folder exists
        if (!Directory.Exists(Program.DataDir)) Directory.CreateDirectory(Program.DataDir);
        // ensure for all tables exists in DB
        foreach (TableTypes tbl in tableTypes)
        {
            try { CreateTableSchemaIfNotEx(tbl); }
            catch (Exception e) { MessageBox.Show(e.Message); Application.Exit(); return; }
        }
        try { CreateIndexSchemaIfNotEx(); }
        catch (Exception e) { MessageBox.Show(e.Message); Application.Exit(); return; }
    }

    /// <summary>
    /// Re-create empty table if not exists.
    /// </summary>
    /// <param name="dict">Dict to re-creating.</param>
    private void CreateTableSchemaIfNotEx(TableTypes tbl)
    {
        string currConStr = tbl.Type == TableTypesGeneral.TH ? ConStrDct : ConStrDoc; // TH : MT

        using (SQLiteConnection con = new SQLiteConnection(currConStr))
        using (SQLiteCommand cmd = new SQLiteCommand(con))
        {
            if (tbl == TableTypes.TH_users) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS TH_users (
                userID   TEXT          NOT NULL  UNIQUE,
            userName TEXT,
            PRIMARY KEY (
                userID
        )
                        );
            ";
                else if (tbl == TableTypes.TH_clients) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS TH_clients (
                cli_code INTEGER       NOT NULL,
                cli_type TEXT          NOT NULL,
                Name     TEXT,
                PRIMARY KEY (
                cli_code,
                cli_type
        )
                        );
            ";
                else if (tbl == TableTypes.TH_objects) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS TH_objects (
                obj_code INTEGER       NOT NULL,
                obj_type TEXT          NOT NULL,
                Name     TEXT,
                PRIMARY KEY (
                obj_code,
                obj_type
        )
                        );
            ";
                else if (tbl == TableTypes.TH_barcodes) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS TH_barcodes (
                GoodsID  INTEGER   NOT NULL,
                BC       TEXT      NOT NULL,
                PriceBC  DOUBLE,
                UnitBC   TEXT,
                UnitRate DOUBLE,
                PRIMARY KEY (
                BC
        )
                        );
            ";
                else if (tbl == TableTypes.TH_goods) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS TH_goods (
                GoodsID   INTEGER   NOT NULL  UNIQUE,
            Name      TEXT,
            UnitBase  TEXT,
            PriceBase DOUBLE,
            VAT       DOUBLE,
            Country   TEXT,
            Struct    TEXT,
            FactQnty  DOUBLE,
            FreeQnty  DOUBLE,
            PRIMARY KEY (
                GoodsID
        )
                        );
            ";
                else if (tbl == TableTypes.MT_documents) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS MT_documents (
                DocName    TEXT     NOT NULL UNIQUE,
            DocType    TEXT,
            Complete   BOOLEAN,
            Status     TEXT,
            ClientID   INTEGER,
            ClientType TEXT,
            ObjectID   INTEGER,
            ObjectType TEXT,
            UserID     TEXT,
            UserName   TEXT,
            FactSum    DOUBLE,
            StartDate  DATETIME,
            Flags      INTEGER,
            PRIMARY KEY (
                DocName
        )
                        );
            ";
                else if (tbl == TableTypes.MT_lines) cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS MT_lines (
                LineID    INTEGER NOT NULL
                UNIQUE,
            DocName   TEXT    NOT NULL,
            Pos       INTEGER,
            GoodsID   INTEGER NOT NULL,
            GoodsName TEXT,
            UnitBC    TEXT,
            BC        TEXT    NOT NULL,
            Price     DOUBLE,
            DocQnty   DOUBLE,
            FactQnty  DOUBLE,
            AlcCode   TEXT,
            PartIDTH  TEXT,
            Flags     INTEGER,
            PRIMARY KEY (
                LineID
        )
                        );
            ";
                else if (tbl == TableTypes.MT_MarkLines)
        {
            cmd.CommandText = @"
            CREATE TABLE IF NOT EXISTS MT_MarkLines (
                LineID   INTEGER   PRIMARY KEY
                UNIQUE
            NOT NULL,
            DocName  TEXT      NOT NULL,
            MarkCode TEXT (150) NOT NULL,
            PartIDTH TEXT      NOT NULL,
            Sts      TEXT (30),
                MarkParent TEXT (30),
                BoxQnty INTEGER
                        );
            ";
        };


            con.Open();
            try { cmd.ExecuteNonQuery(); }
            catch (SQLiteException)
            {
                throw new Exception(
                        "Файл базы данных поврежден."
                                + "\nПопробуйте удалить файл "
                                + (tbl.Type == TableTypesGeneral.TH ? dbPathDct : dbPathDoc)
                );
            }
        }
    }

    private void CreateIndexSchemaIfNotEx()
    {

        string currConStr = ConStrDoc;  // TH : MT

        SQLiteConnection con = new SQLiteConnection(currConStr);
        SQLiteCommand cmd = new SQLiteCommand(con);

        cmd.CommandText = @"
        CREATE INDEX IF NOT EXISTS pi ON MT_MarkLines (
            DocName,
            MarkCode
                );

        CREATE INDEX IF NOT EXISTS markCode ON MT_MarkLines (
            MarkCode ASC
                );

        CREATE INDEX IF NOT EXISTS pi ON MT_MarkLines (
            DocName,
            MarkParent
                );

        CREATE INDEX IF NOT EXISTS markCode ON MT_MarkLines (
            MarkParent ASC
                );

        CREATE INDEX IF NOT EXISTS DocName ON MT_MarkLines (
            DocName ASC
                );

        CREATE UNIQUE INDEX IF NOT EXISTS DocNamePartId ON MT_MarkLines (
            DocName ASC,
            MarkCode ASC,
            PartIDTH ASC
    );

        CREATE UNIQUE INDEX IF NOT EXISTS Line ON MT_MarkLines (
            LineID ASC,
            DocName ASC
    );

        CREATE UNIQUE INDEX IF NOT EXISTS markline ON MT_MarkLines (
            LineID ASC,
            MarkCode ASC
    );

        CREATE UNIQUE INDEX IF NOT EXISTS docNameDoc ON MT_documents (
            DocName ASC,
            DocType ASC
    );

        CREATE INDEX IF NOT EXISTS lDocName ON MT_lines (
            DocName ASC
                );

        CREATE INDEX IF NOT EXISTS lDocNameGdsUnit ON MT_lines (
            DocName ASC,
            GoodsID ASC,
            UnitBC ASC
                );

        CREATE INDEX IF NOT EXISTS lDocNameBC ON MT_lines (
            DocName ASC,
            BC ASC
                );

        CREATE INDEX IF NOT EXISTS lDocNameAlc ON MT_lines (
            DocName ASC,
            AlcCode ASC
                );

        CREATE UNIQUE INDEX IF NOT EXISTS lLine ON MT_lines (
            LineID ASC
    );

        CREATE INDEX IF NOT EXISTS lDocNPart ON MT_lines (
            DocName ASC,
            PartIDTH ASC
                );

        ";


        con.Open();
        try { cmd.ExecuteNonQuery(); }
        catch (SQLiteException myException)
        {
            throw new Exception(
                    "Файл базы данных поврежден."
                            + "\nПопробуйте удалить файл "
                            + myException.Message
            );
        };


        con.Dispose();
        cmd.Dispose();

        currConStr = ConStrDct;
        con = new SQLiteConnection(currConStr);
        cmd = new SQLiteCommand(con);
        cmd.CommandText = @"
        CREATE INDEX IF NOT EXISTS BC ON TH_barcodes (
            BC ASC
                );

        CREATE INDEX IF NOT EXISTS gds ON TH_barcodes (
            GoodsID ASC
                );

        CREATE UNIQUE INDEX IF NOT EXISTS cli ON TH_clients (
            cli_type ASC,
            cli_code ASC
    );

        CREATE UNIQUE INDEX IF NOT EXISTS gds ON TH_goods (
            GoodsID ASC
    );

        CREATE INDEX IF NOT EXISTS obj ON TH_objects (
            obj_type ASC,
            obj_code ASC
                );

        CREATE UNIQUE INDEX IF NOT EXISTS gdsId ON TH_goods (
            GoodsID ASC
    );
        ";

        con.Open();
        try { cmd.ExecuteNonQuery(); }
        catch (SQLiteException myException)
        {
            throw new Exception(
                    "Файл базы данных поврежден."
                            + "\nПопробуйте удалить файл "
                            + myException.Message
            );
        };

    }


    // Send document to remote server. Remove it if sending is successfull.
    // TODO: REPLACE RETCODES BY ENUMS!!!!
    /// <returns>0 - SEND OK, 1 - SEND NOT ACCEPTED, 2 - Server sends unexpected response, 3 - Server not responds.</returns>
    public int SendDocument(DocumentsDataSet.MT_documentsRow docRow)
    {
        Cursor.Current = Cursors.WaitCursor;

        XmlDocument responseXML = Connectivity.sendMTDoc(docRow);
        if (responseXML == null)
        {
            Cursor.Current = Cursors.Default;
            return 3; // connectivity problem
        }

        try // getting child nodes can cause exceptions
        {
            if (responseXML.DocumentElement.GetElementsByTagName("STATUS")[0].InnerText == "ERROR")
            {
                Cursor.Current = Cursors.Default;
                //MessageBox.Show(responseXML.DocumentElement.GetElementsByTagName("STATUS_MESSAGE")[0].InnerText);
                return 1; // server says NOT OK
            }

            if (responseXML.DocumentElement.GetElementsByTagName("STATUS")[0].InnerText != "OK")
            {
                Cursor.Current = Cursors.Default;
                return 2; // some troubles on server's answer
            }
        }
        catch (Exception)
        {
            Cursor.Current = Cursors.Default;
            //MessageBox.Show("Некорректный ответ сервера.");
            return 2; // some troubles on server's answer
        }

        // else all is OK

        // not delete inventories after sending!
        if (Storage.DocTypes.Parse(docRow.DocType) != Storage.DocTypes.Inv)
            Program.storage.RemoveDocument(docRow);

        Cursor.Current = Cursors.Default;
        return 0; // server says OK
    }

    // Remove document and its lines from both DB and DataTable.
    public void RemoveDocument(DocumentsDataSet.MT_documentsRow docRow)
    {
        // delete children from DB directly
        using (SQLiteConnection con = new SQLiteConnection(ConStrDoc))
        using (SQLiteCommand cmd = new SQLiteCommand(con))
        {
            cmd.CommandText = "delete from MT_lines where DocName = \"" + docRow.DocName + "\""
                    + @";" + "\n " +

                "delete from MT_MarkLines where DocName = \"" + docRow.DocName + "\""
                + @";" + "\n ";
            con.Open();
            try { cmd.ExecuteNonQuery(); }
            catch (SQLiteException myException)
            {
                MessageBox.Show(
                        "Ошибка при удалении документа."
                                + "\n " + myException.Message
                                + "\n " + docRow.DocName

                );
            }

        }
        using (MT_documentsTableAdapter TA =
                new MT_documentsTableAdapter(Program.storage.ConStrDoc))
        {
            TA.Delete(docRow.DocName);
        }
        if (Program.storage.dsDoc.MT_documents.Contains(docRow))
            Program.storage.dsDoc.MT_documents.RemoveMT_documentsRow(docRow);

            /*using (MT_linesTableAdapter TA =
                new MT_linesTableAdapter(Program.storage.ConStrDoc))
            {
                TA.Fill(Program.storage.dsDoc.MT_lines);
            }
            DocumentsDataSet.MT_linesRow[] docLinesRows = docRow.GetMT_linesRows().ToArray();
            for (int i = 0; i < docLinesRows.Length; i++)
            {
                RemoveLineRow(docLinesRows[i]);
            }

            // delete parent from DB directly
            using (MT_documentsTableAdapter TA =
                new MT_documentsTableAdapter(Program.storage.ConStrDoc))
            {
                TA.Delete(docRow.DocName);
            }
            // remove parent from DataTable if exists
            if (Program.storage.dsDoc.MT_documents.Contains(docRow))
                Program.storage.dsDoc.MT_documents.RemoveMT_documentsRow(docRow);*/
    }
    public void RemoveLineRow(DocumentsDataSet.MT_linesRow lineRow)
    {
        // delete children from DB directly
        using (MT_MarkLinesTableAdapter TA =
                new MT_MarkLinesTableAdapter(Program.storage.ConStrDoc))
        {
            TA.DeleteRowsByMark(lineRow.DocName, lineRow.PartIDTH);
        }
            /*// remove children from DataTable if exists
            lineRow.GetMT_MarkLinesRows()
                .ForEach(row =>
                    Program.storage.dsDoc.MT_MarkLines.RemoveMT_MarkLinesRow(row)
            );*/

        // delete parent from DB directly
        using (MT_linesTableAdapter TA =
                new MT_linesTableAdapter(Program.storage.ConStrDoc))
        {
            TA.DeleteRowsBy(lineRow.DocName);
        }
        // remove parent from DataTable if exists
        if (Program.storage.dsDoc.MT_lines.Contains(lineRow))
            Program.storage.dsDoc.MT_lines.RemoveMT_linesRow(lineRow);
    }

    public enum LoadDBStatus // make class enum
    {
        SUCCESS = 0,
        CONN_FAIL = 1,
        SERVER_NA = 2,
        BAD_PARSING = 3,
        FILE_BUSY = 4
    }
    public static Dictionary<LoadDBStatus, string> dbStatusDescriptions
            = new Dictionary<LoadDBStatus, string>()
    {
        { LoadDBStatus.SUCCESS, "Обновление успешно завершено." },
        { LoadDBStatus.CONN_FAIL, "Не удается подключиться к серверу." },
        { LoadDBStatus.SERVER_NA, "Сервер не отвечает." },
        { LoadDBStatus.BAD_PARSING, "Ответ сервера содержит ошибки." },
        { LoadDBStatus.FILE_BUSY, "Не удается записать файл хранилища." }
    };
    public LoadDBStatus DownloadDictFile()
    {
        if (ActionStarting != null) ActionStarting(this, new ActionEventArgs(Actions.LOAD_DB));

        string tmpDBPath = Path.Combine(Program.DataDir, "tmp.s3db");
        int res = Connectivity.loadDBFile(tmpDBPath);
        if (res > 0 || !File.Exists(tmpDBPath))
        {
            File.Delete(tmpDBPath);

            if (ActionFinished != null) ActionFinished(this, new ActionEventArgs(Actions.LOAD_DB));
            return (LoadDBStatus)res;
        }

        try
        {
            File.Delete(dbPathDct);
            File.Move(tmpDBPath, dbPathDct);
        }
        catch (Exception) // should never exec
        {
            MessageBox.Show("Ошибка доступа к файлам хранилища.");
            return LoadDBStatus.FILE_BUSY;
        }

        if (ActionFinished != null) ActionFinished(this, new ActionEventArgs(Actions.LOAD_DB));
        return LoadDBStatus.SUCCESS;
    }


    private class RcvDocInfo
    {
        public string DocName;
        public bool alreadyExists;
    }
    private class DocAux
    {
        public List<RcvDocInfo> rcvDocList;
    }
    public LoadDBStatus DownloadDocs()
    {
        if (ActionStarting != null) ActionStarting(this, new ActionEventArgs(Actions.LOAD_DOC));

        //DocAux stats = new DocAux(); stats.rcvDocList = new List<RcvDocInfo>();

        string tmpDBPath = Path.Combine(Program.DataDir, "tmpDoc.s3db");
        int res = Connectivity.loadWBDocs(tmpDBPath);
        if (res > 0 || !File.Exists(tmpDBPath))
        {
            File.Delete(tmpDBPath);

            if (ActionFinished != null) ActionFinished(this, new ActionEventArgs(Actions.LOAD_DOC));
            return (LoadDBStatus)res;
        }
        try
        {
            File.Delete(dbPathDoc);
            File.Move(tmpDBPath, dbPathDoc);
        }
        catch (Exception) // should never exec
        {

            if (ActionFinished != null) ActionFinished(this, new ActionEventArgs(Actions.LOAD_DOC));
            MessageBox.Show("Ошибка доступа к файлам хранилища.");
            return LoadDBStatus.FILE_BUSY;
        }

        if (ActionFinished != null) ActionFinished(this, new ActionEventArgs(Actions.LOAD_DOC));

        return LoadDBStatus.SUCCESS;
    }
    private int xmlDocParser(StringBuilder strb, object options)
    {
        Int32 processedChars = 0; // processed string will be cutted by this amount
        // allow to parse fragments instead of well-formed document
        // for avoiding exception "too many root elements"
        XmlReaderSettings xmlSettings = new XmlReaderSettings();
        xmlSettings.ConformanceLevel = ConformanceLevel.Fragment;
        xmlSettings.IgnoreWhitespace = true;

        using (StringReader sr = new StringReader(strb.ToString()))
        using (XmlReader xmlRdr = XmlReader.Create(sr, xmlSettings))
        {
            IXmlLineInfo xli = (IXmlLineInfo)xmlRdr; // allow to get current position
            DocAux auxObj = (DocAux)options; // auxiliary object for storing some useful data

            bool read = true; // initial
            while (read)
            {
                try { read = xmlRdr.Read(); }
                catch (Exception) // smth like "unexpected end"
                {
                    // XmlReader can't get end position of current element (especially self-closed).
                    // When next Read() causes exception "unexpected end..." reader position will be EOF
                    // So use SHITTY WORKAROUND!
                    Int32 tagEndPos = strb.ToString().IndexOf("/>", processedChars);
                    if (tagEndPos != -1)
                        processedChars = tagEndPos + "/>".Length;
                    return processedChars;
                }

                // note: line position and line number started from 1.
                switch (xmlRdr.NodeType) // set processed characters
                {
                    case XmlNodeType.XmlDeclaration:
                        break;
                    case XmlNodeType.Element: // <[linePos]ElementTag>
                        processedChars = xli.LinePosition - 2; break;
                    case XmlNodeType.EndElement: // </[linePos]EndElementTag>
                        processedChars = xli.LinePosition - 3; break;
                    case XmlNodeType.None: // lastElementTag>[linePos]
                        processedChars = xli.LinePosition - 1; break;
                }

                if (xmlRdr.NodeType != XmlNodeType.Element) continue;

                if (xmlRdr.Name == "header")
                {
                    DocumentsDataSet.MT_documentsRow newDocRow =
                            Program.storage.dsDoc.MT_documents.NewMT_documentsRow();
                    Program.lineOrdinal = 0;

                    for (int i = 0; i < xmlRdr.AttributeCount; i++)
                    {
                        //TODO: replace to GetAttribute()
                        xmlRdr.MoveToAttribute(i);
                        switch (xmlRdr.Name)
                        {
                            case "DocName": // primary key
                            case "DocType":
                            case "ClientID":
                            case "ClientType":
                            case "ObjectID":
                            case "ObjectType":
                            case "StartDate":
                            case "userID": //TODO: FIX lettercase ON SERVER!
                                newDocRow[xmlRdr.Name] = xmlRdr.Value;
                                break;
                            // other data will be ignored
                        }
                    }

                    newDocRow.Complete = false;
                    newDocRow.FactSum = 0;
                    if (newDocRow.DocType == DocTypes.ExtPurchWB ||
                            newDocRow.DocType == DocTypes.IntPurchWB)
                        newDocRow.Status = "НАКЛ+";
                    else newDocRow.Status = "РАЗР+";

                    try { newDocRow.UserName = DBHelper.GetUser(newDocRow.UserID).userName; }
                    catch (DBHelper.DBException) { /* leave null value */ }

                    // store received docInfo
                    RcvDocInfo rdi = new RcvDocInfo();
                    rdi.DocName = newDocRow.DocName;
                    rdi.alreadyExists = false;

                    // TODO: make select first, next insert without try/catch
                    try
                    {
                        using (MT_documentsTableAdapter TA =
                                new MT_documentsTableAdapter(Program.storage.ConStrDoc))
                        { TA.DirectInsertNonQuery(newDocRow); }
                    }
                    // UNIQUE constraint may be failed in PK
                    catch (SQLiteException) { rdi.alreadyExists = true; }

                    auxObj.rcvDocList.Add(rdi);
                }

                else if (xmlRdr.Name == "line")
                {
                    DocumentsDataSet.MT_linesRow newLineRow =
                            Program.storage.dsDoc.MT_lines.NewMT_linesRow();
                    for (int i = 0; i < xmlRdr.AttributeCount; i++)
                    {
                        xmlRdr.MoveToAttribute(i);
                        switch (xmlRdr.Name)
                        {
                            case "DocName": // foreign key
                            case "GoodsID":
                            case "UnitBC":
                            case "BC":
                            case "Price":
                            case "DocQnty":
                            case "AlcCode":
                            case "PartIDTH":
                                newLineRow[xmlRdr.Name] = xmlRdr.Value;
                                break;
                            // other data will be ignored
                        }
                    }
                    Program.lineOrdinal++;
                    RcvDocInfo relatedDocInfo = auxObj.rcvDocList.Find(
                            info => info.DocName == newLineRow.DocName);
                    if (relatedDocInfo == null) continue; // ignore <line>s with no matched <header>
                    if (relatedDocInfo.alreadyExists) continue; // skip lines which belongs to already existed (ignored) docs

                    // TODO: make logging of non-critical exceptions
                    DBHelper.BCInfo bcInfo;
                    try { bcInfo = DBHelper.GetBCInfo(newLineRow.BC); }
                    catch (DBHelper.DBException) { continue; } // skip not finded barcodes
                    newLineRow.GoodsName = bcInfo.gdRow.Name;

                    //newLineRow.LineID = 0; // autoincrement
                    newLineRow.Pos = Program.lineOrdinal; // ordinal of current row in WB (not used and deprecated)
                    newLineRow.FactQnty = 0;

                    // not causes UNIQUE constraint exceptions (autoincrement PK)
                    using (MT_linesTableAdapter TA =
                            new MT_linesTableAdapter(Program.storage.ConStrDoc))
                    {
                        TA.DirectInsertNonQuery(newLineRow);
                    }

                }
                else if (xmlRdr.Name == "MarkLines")
                {
                    DocumentsDataSet.MT_MarkLinesRow newMarkLineRow =
                            Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
                    for (int i = 0; i < xmlRdr.AttributeCount; i++)
                    {
                        xmlRdr.MoveToAttribute(i);
                        switch (xmlRdr.Name)
                        {
                            case "MarkCode":
                            case "DocName":
                            case "PartIDTH":
                            case "Sts":
                                newMarkLineRow[xmlRdr.Name] = xmlRdr.Value;
                                break;
                            // other data will be ignored
                        }
                    }
                    RcvDocInfo relatedDocInfo = auxObj.rcvDocList.Find(
                            info => info.DocName == newMarkLineRow.DocName);
                    if (relatedDocInfo == null) continue; // ignore <line>s with no matched <header>
                    if (relatedDocInfo.alreadyExists) continue; // skip lines which belongs to already existed (ignored) docs

                    // not causes UNIQUE constraint exceptions (autoincrement PK)
                    using (MT_MarkLinesTableAdapter TA =
                            new MT_MarkLinesTableAdapter(Program.storage.ConStrDoc))
                    {
                        TA.DirectInsertNonQuery(newMarkLineRow);
                    }

                }

            }

        }

        return processedChars;
    }
}



static class DBHelper
{
    // TODO: make enum-like class with codes descriptions
    public enum DB_ERR_CODES
    {
        ALL_OK = 0,
        BC_NOT_FOUND = 1,
        GOODS_NOT_FOUND = 2,
        USER_NOT_FOUND = 3
    };
    public class DBException : Exception
{
    // own data section
    private DB_ERR_CODES _errCode = DB_ERR_CODES.ALL_OK;
    public DB_ERR_CODES ErrCode { get { return _errCode; } }
    // ctors
            public DBException() : base() { }
            public DBException(DB_ERR_CODES code)
                : base(DB_ERR_MSGS[(int)code]) { _errCode = code; }
            public DBException(DB_ERR_CODES code, Exception innerEx)
                : base(DB_ERR_MSGS[(int)code], innerEx) { _errCode = code; }

}

    public static string DecodeMark(string input)
    {
        string CharList = "0123456789abcdefghijklmnopqrstuvwxyz";
        string tempStr = "";
        input = input.Substring(7, 12);
        var reversed = input.ToLower().Reverse();
        long result = 0;
        int pos = 0;
        foreach (char c in reversed)
        {
            result += (CharList.IndexOf(c) * (long)Math.Pow(36, pos));
            pos++;
        }
        for (int i = 0; i < 19 - result.ToString().Length; i++)
        {
            tempStr = "0" + tempStr;
        }
        return tempStr + result.ToString();
    }

    public struct BCInfo
    {
        public DictionariesDataSet.TH_barcodesRow bcRow;
        public DictionariesDataSet.TH_goodsRow gdRow;
        public int weight; // in gramms, 5 digits. -1 if not weighted BC
    }
    public static BCInfo GetBCInfo(string bc)
    {
        BCInfo bcInfo;

        // not weighted BC (default)
        bcInfo.weight = -1;
        string paramBC = bc;

        if ((Program.cfg.Options & Config.OPTIONS.NO_WGHT_PFX) == 0 && // weighted
                Program.cfg.BCWeightPrefixes.Contains(bc.Substring(0, 2)))
        {
            // 2 digits for barcode prefix (weight pfx)
            // 5 digits for item code
            // 5 digits for item weight (in gramms)
            // 1 digit  for checksum
            paramBC = bc.Substring(2, 5);
            bcInfo.weight = int.Parse(bc.Substring(7, 5));
        }

        using (TH_barcodesTableAdapter TA =
                new TH_barcodesTableAdapter(Program.storage.ConStrDct))
        {
            DictionariesDataSet.TH_barcodesDataTable bcTbl = TA.GetDataBy(paramBC);
            if (bcTbl.Count == 0)
                throw new DBException(DB_ERR_CODES.BC_NOT_FOUND);
            else bcInfo.bcRow = bcTbl[0];
        }

        using (TH_goodsTableAdapter TA =
                new TH_goodsTableAdapter(Program.storage.ConStrDct))
        {
            DictionariesDataSet.TH_goodsDataTable gdTbl = TA.GetDataBy(bcInfo.bcRow.GoodsID);
            if (gdTbl.Count == 0)
                throw new DBException(DB_ERR_CODES.GOODS_NOT_FOUND);
            else bcInfo.gdRow = gdTbl[0];
        }

        return bcInfo;
    }

    public static long CountUsers()
    {
        using (TH_usersTableAdapter TA =
                new TH_usersTableAdapter(Program.storage.ConStrDct))
        { return (long)TA.Count(); }
    }
    public static DictionariesDataSet.TH_usersRow GetUser(string userId)
    {
        DictionariesDataSet.TH_usersRow userRow = null;

        using (TH_usersTableAdapter TA =
                new TH_usersTableAdapter(Program.storage.ConStrDct))
        {
            DictionariesDataSet.TH_usersDataTable userTbl = TA.GetDataBy1(userId);
            if (userTbl.Count == 0)
                throw new DBException(DB_ERR_CODES.USER_NOT_FOUND);
            else
                userRow = userTbl[0];
        }

        return userRow;
    }

    public static bool IsDocNameExists(string name)
    {
        using (MT_documentsTableAdapter TA =
                new MT_documentsTableAdapter(Program.storage.ConStrDoc))
        { return (long)TA.CountBy(name) > 0; }
    }

    public static bool IsMarkExists(string mark, string docname)
    {
        using (MT_MarkLinesTableAdapter TA =
                new MT_MarkLinesTableAdapter(Program.storage.ConStrDoc))
        { return (long)TA.CountByMark(mark, docname) > 0; }
    }

    public static bool IsMarkExists(string mark, string docname, string prtTHID)
    {
        using (MT_MarkLinesTableAdapter TA =
                new MT_MarkLinesTableAdapter(Program.storage.ConStrDoc))
        { return (long)TA.CountByMarkByPrt(mark, docname, prtTHID) > 0; }
    }

    public static string[] DB_ERR_MSGS =
            {
                    // 0 - not used for printing message
                    "All is OK",
                    // 1
                    "Штрих-код не найден в справочниках.\n",
                    // 2
                    "Товар с данным штрих-кодом не найден в справочниках.\n"
            };
}


}
