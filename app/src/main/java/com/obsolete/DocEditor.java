package com.obsolete;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;

import com.expertek.tradehouse.dictionaries.entity.Obj;

public class DocEditor {
    // Replace current DGTS with new one with Custom DGCS.
    // Added custom properties can be accessed through casting to new type.\
    public bool IsGrayZone;
    public string ParentMark;
    public event EventHandler
    public event EventHandler
    WidgetOpening = null;
    public bool isShown = false;
    WidgetClosed =null;
    public DocumentsDataSet.MT_documentsRow CurrentDocument
    private DocumentsDataSet.MT_linesRow slctdLine = null;
    private DocumentsDat.MT_MarkLinesRow markRow = null;
    //private AtaSerrayList marksWrite = new ArrayList();
    private long pos = 1;
    private int NumLines = 0;
    private bool isMarksReg;
    private string s_GTIN = "";
    private bool isWaitMarks = false;
    //private string alcCode = "";
    private bool isNoAddorDel = false;
    private bool isUTDDoc = false;
    private bool isUTDDocIntroduce = false;

    private DataView dv = new DataView();
    private Storage.DocTypes currDocType = null;
    private DocumentsDataSet.MT_documentsRow currDoc = null;
    private DocumentsDataSet.MT_linesRow editLineRow;
    private MT_linesTableAdapter TALines;
    //private SQLiteTransaction TALinesTrans;
    private SQLiteConnection SQLConn;
    private int LineIdBegin;
    private int LineIdEnd;

    {
        get {
        return currDoc;
    }
        set {
            currDoc = value;
            if (dv != null) dv.RowFilter = "DocName = '" + currDoc.DocName + "'";
            currDocType = Storage.DocTypes.Parse(currDoc.DocType);
            dataGridTextBoxColumnPrice.Width = // price column
                    currDoc.DocType == Storage.DocTypes.Inv ? 0 : 35;
        }
    }

    public DocEditor() {
        InitializeComponent();
        InitializeDataGrid();

        buttonManualInput.Image = MT.Properties.Resources.Creating_1_add;
        buttonEdit.Image = MT.Properties.Resources.Creating_2_edit;
        buttonSave.Image = MT.Properties.Resources.Creating_3_save;
        buttonFinish.Image = MT.Properties.Resources.Creating_4_finish;
        buttonManualInput.DisabledImage = MT.Properties.Resources.Creating_1_add_Disabled;
        buttonEdit.DisabledImage = MT.Properties.Resources.Creating_2_edit_Disabled;
        buttonSave.DisabledImage = MT.Properties.Resources.Creating_3_save_Disabled;
        buttonFinish.DisabledImage = MT.Properties.Resources.Creating_4_finish_Disabled;

        this.Visible = false;
    }

    private void InitializeDataGrid() {
        DataGridTableStyle oldStyle = dataGridTableStyleLines;

        SuspendLayout();
        dataGridTableStyleLines = new DataGridTableStyle();
        dataGridTextBoxColumnPos = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnPos);
        dataGridTextBoxColumnGoods = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnGoods);
        dataGridTextBoxColumnUnit = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnUnit);
        dataGridTextBoxColumnPrice = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnPrice);
        dataGridTextBoxColumnFactQnty = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnFactQnty);
        dataGridTextBoxColumnDocQnty = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnDocQnty);
        dataGridTextBoxColumnBC = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnBC);
        dataGridTextBoxColumnSum = new DataGridTextBoxColumnCustom(dataGrid1, dataGridTextBoxColumnSum);

        dataGrid1.TableStyles.Remove(oldStyle);
        dataGrid1.TableStyles.Add(dataGridTableStyleLines);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnPos);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnGoods);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnUnit);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnPrice);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnFactQnty);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnDocQnty);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnBC);
        dataGridTableStyleLines.GridColumnStyles.Add(dataGridTextBoxColumnSum);
        dataGridTableStyleLines.MappingName = oldStyle.MappingName;
        ResumeLayout(false);
    }

    public int AvailableLines(DocumentsDataSet.MT_documentsRow docRow) {
        string expr = "DocName = '" + docRow.DocName + "'";
        return Program.storage.dsDoc.MT_lines.Select(expr).Length;
    }

    private void SetModal(bool modal) {
        if (isNoAddorDel) {
            buttonManualInput.Enabled = false;
        } else {
            buttonManualInput.Enabled = !modal;
        }

        buttonEdit.Enabled = !modal;
        buttonSave.Enabled = !modal;
        buttonFinish.Enabled = !modal;
        PageUpDown.Enabled = !modal;

        if (dv.Count > 0) {
            DocumentsDataSet.MT_linesRow row1;
            row1 = (DocumentsDataSet.MT_linesRow) (dv[0].Row);
            if (row1.AlcCode != "" && isMarksReg) {
                this.buttonManualInput.Enabled = false;
            }
        }
    }

    private void TrySetCurLine(int idx) {
        if (dv.Count > 0 && idx <= dv.Count - 1) {
            slctdLine = (DocumentsDataSet.MT_linesRow) (dv[0].Row);
            dataGrid1.ForceCurrentCellChangedEvent(idx);
        } else {
            slctdLine = null;
        }
    }

    // TODO: cut off
    private long NextPos() // calculate next "Pos" for inserting
    {
        Obj tmp = dv.Table.Compute("Max(Pos)",
                "DocName = '" + currDoc.DocName + "'"
        );
        return (tmp == DBNull.Value ? 1 : (long) tmp + 1);
    }

    private double RefreshSum() // calculate current sum and store it
    {
        double sum;
        Obj tmp = 0;
        if (currDocType == "Inv") {
            using(MT_linesTableAdapter TA = new MT_linesTableAdapter())
            try {
                TA.Connection = SQLConn;
                try {
                    tmp = TA.SumFactQnty(currDoc.DocName);
                } catch (SQLiteException myException) {
                    MessageBox.Show(myException.Message);
                }
            } finally {
                TA.Connection = null;
                TA.Dispose();
            }
        } else {
            using(MT_linesTableAdapter TA = new MT_linesTableAdapter())
            try {
                TA.Connection = SQLConn;
                try {
                    tmp = TA.SumFact(currDoc.DocName);
                } catch (SQLiteException myException) {
                    MessageBox.Show(myException.Message);
                }
            } finally {
                TA.Connection = null;
                TA.Dispose();
            }
        }
        sum = (tmp == DBNull.Value ? 0.0 : (double) tmp);
        currDoc.FactSum = sum;
        using(MT_documentsTableAdapter
                TA = new MT_documentsTableAdapter())
        try {
            TA.Connection = SQLConn;
            try {
                TA.Update(currDoc);
            } catch (SQLiteException myException) {
                MessageBox.Show(myException.Message);
            }
        } finally {
            TA.Connection = null;
            TA.Dispose();
        }
        lblTotalSum.Text = sum.ToString("N2");
        return sum;
    }

    public int LoadData() {
        if (NumLines == 0) {
            return 0;
        }

        if (!Program.storage.dsDoc.MT_lines.Columns.Contains("RowSum"))
            Program.storage.dsDoc.MT_lines.Columns.Add("RowSum", typeof( double),"FactQnty * Price")
        ;

        Cursor.Current = Cursors.WaitCursor;

        TALines.FillByDocNameMoreLineId(0, Program.cfg.N_Pages, Program.storage.dsDoc.MT_lines, currDoc.DocName, LineIdBegin + (Program.cfg.N_Pages * ((int) PageUpDown.Value - 1)));
        Cursor.Current = Cursors.Default;

        dv.Table = Program.storage.dsDoc.MT_lines;
        dv.Sort = "DocName, Pos";
        dataGrid1.DataSource = dv;
        return Program.storage.dsDoc.MT_lines.Count;
    }

    public int LoadData(DocumentsDataSet.MT_documentsRow docRow) {
        this.CurrentDocument = docRow;
        SQLConn = new SQLiteConnection(Program.storage.ConStrDoc);
        SQLConn.Open();
        TALines = new MT_linesTableAdapter();
        TALines.Connection = SQLConn;
        PageUpDown.Value = 1;
        isMarksReg = Program.cfg.MarksReg;
        if (this.CurrentDocument.Status == "НОВ" || this.CurrentDocument.Status == "разрешен+")
            isMarksReg = false;

        isMarksReg = (((byte) CurrentDocument.Flags) & ((byte) Storage.DocFlags.isDocMarkOn)) == 1;

        return this.LoadDataInit();
    }

    // кто будет трогать ребёнка (и детей) остановлю всё, потеряем все шансы
    // всё сделается само, если будет возможно, ну а я попробую не навредить
    // НЕЗАВИСИМО от того, что привиделось 4-осному зрению, заточенному на fuck
    // У детей от домогательств не выдерживает психика, да и совесть ваша где.
    // Потому что это уже проходили, чревато пиздецом. Я ЗНАЮ что это верно.
    // Кто изолирует всех дегенератов с секс-расстройством, будет молодец.
    public int LoadDataInit() {
        //TALinesTrans = TALines.Connection.BeginTransaction();

        Cursor.Current = Cursors.WaitCursor;

        NumLines = Convert.ToInt16(TALines.CoutByDoc(currDoc.DocName));
        int npage = 1;
        decimal npaged = 1;
        if (NumLines == 0) {
            TALines.FillByDoc(Program.storage.dsDoc.MT_lines, currDoc.DocName);
            Cursor.Current = Cursors.Default;
        } else {
            //TALines.FillByDoc(0, 1, Program.storage.dsDoc.MT_lines, currDoc.DocName);
            TALines.FillByDoc(Program.storage.dsDoc.MT_lines, currDoc.DocName);
            LineIdBegin = Convert.ToInt32(Program.storage.dsDoc.MT_lines[0].LineID);
            TALines.FillByDoc(NumLines - 1, 1, Program.storage.dsDoc.MT_lines, currDoc.DocName);
            LineIdEnd = Convert.ToInt32(Program.storage.dsDoc.MT_lines[0].LineID);
            Cursor.Current = Cursors.Default;
            npaged = Convert.ToDecimal(LineIdEnd - LineIdBegin + 1) / Convert.ToDecimal(Program.cfg.N_Pages);
            npage = (int) (npaged);
        }

        if ((decimal) npage < npaged) {
            npage = npage + 1;
        }
        if (npage == 1) {
            PageUpDown.Visible = false;
            lblNpage.Visible = false;
            lblNp.Visible = false;

        } else {
            PageUpDown.Visible = true;
            lblNpage.Visible = true;
            lblNp.Visible = true;
            lblNp.Text = (npage.ToString());
            PageUpDown.Maximum = npage;
        }
        return this.LoadData();
    }

    public void ShowWidget() {
        if (WidgetOpening != null) WidgetOpening(this, EventArgs.Empty);

        Rectangle parentRect = this.Parent.ClientRectangle;
        this.Location = parentRect.Location;
        this.Size = parentRect.Size;

        Program.tsd.Callback = BC_processing;
        TrySetCurLine(0);

        //this.textBoxDocName.TextChanged -= textBoxDocName_TextChanged;
        this.textBoxDocName.Text = currDoc.DocName;
        this.textBoxDocName.ForeColor = Color.Black;
        if (this.CurrentDocument.Status == "разрешен+" || this.CurrentDocument.Status == "накл+" || currDocType == Storage.DocTypes.UTD || currDocType == Storage.DocTypes.InvMarks || currDocType == Storage.DocTypes.InvIntro) {
            isNoAddorDel = true;
            buttonManualInput.Enabled = false;
            this.textBoxDocName.Enabled = false;
        } else {
            isNoAddorDel = false;
            buttonManualInput.Enabled = true;
            this.textBoxDocName.Enabled = false;
        }
        // Preparing document
        this.lblDocDate.Text = " от " + currDoc.StartDate.ToString( @ "dd.MM.yyyy");
        this.isShown = true;

        if (dv.Count > 0) {
            DocumentsDataSet.MT_linesRow row1;
            row1 = (DocumentsDataSet.MT_linesRow) (dv[0].Row);
            if (row1.AlcCode != "" && isMarksReg) {
                this.buttonManualInput.Enabled = false;
            }
        }

        this.BringToFront();
        this.Visible = true;
    }

    private void CloseWidget() {
        Program.tsd.Callback = null;

        if (docLineEditor1.Visible && docLineEditor1.IsInitialEdit) {
            slctdLine.Delete();
        }

        SQLConn.Close();
        SQLConn.Dispose();
        TALines.Dispose();
        this.isShown = false;
        this.Visible = false;
        if (WidgetClosed != null) WidgetClosed(this, EventArgs.Empty);
    }

    public void TryCloseWidget() {
        if (manualBCInput1.Visible) {
            manualBCInput1.CloseWidget();
            return;
        }
        if (docLineEditor1.Visible) {
            docLineEditor1.CloseWidget();
            return;
        }
        CloseWidget();
    }

    // slightly different for Invs and WBs
    private void BC_processing(string bc) {
        DocumentsDataSet.MT_linesRow[] foundLinesRows = null;
        markRow = Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
        DBHelper.BCInfo bcInfo;
        bcInfo.bcRow = null;
        bcInfo.gdRow = null;
        bcInfo.weight = 0;
        bool isWaitMarksDoc = false;
        string vMarkShort = "";
        bool isExists = false;
        vMarkShort = Storage.GetCodeIdent(bc);

        // isEditable
        if (currDocType == Storage.DocTypes.UTD || currDocType == Storage.DocTypes.InvMarks || currDocType == Storage.DocTypes.InvIntro) {
            if (vMarkShort != "") {
                bc = vMarkShort;
            }
            isUTDDoc = true;
        }

        if (currDocType == Storage.DocTypes.Introduce) {
            isUTDDocIntroduce = true;
        }

        if (isUTDDocIntroduce || currDocType == Storage.DocTypes.InvIntro || IsGrayZone) {
            s_GTIN = Storage.getGtinByDM(bc);

            try {
                bcInfo = DBHelper.GetBCInfo(s_GTIN);
            } catch (DBHelper.DBException e) {
                Program.tsd.ScannerEnabled = false; // temporary switch-off scanner for preventing many blind scans
                MessageBox.Show(e.Message + " GTIN не найден в справочнике товаров. Обновите справочники.");
                Program.tsd.ScannerEnabled = true;
                return;
            }

            if ((currDocType == Storage.DocTypes.Introduce || IsGrayZone) && scanMarks1.Visible == true && scanMarks1.EditedLine.BC != s_GTIN
                    && !(scanMarks1.EditedLine.GoodsID == bcInfo.gdRow.GoodsID && IsGrayZone)) {
                Program.tsd.ScannerEnabled = false;
                msgshow("Товар GTIN - " + scanMarks1.EditedLine.BC + " не совпадает с GTIN - " + s_GTIN + " выбранной линии.");
                Program.tsd.ScannerEnabled = true;
                return;
            }

            using(DocumentsDataSet.MT_linesDataTable MT_DT = new DocumentsDataSet.MT_linesDataTable()) {
                try {
                    TALines.FillByDocGdsUnit(MT_DT, currDoc.DocName, bcInfo.gdRow.GoodsID, bcInfo.bcRow.UnitBC);
                } catch (SQLiteException myException) {
                    MessageBox.Show(myException.Message);
                    return;
                }
                isExists = MT_DT.Count > 0;
            }

            if (!isExists && currDocType != Storage.DocTypes.InvIntro && currDocType != Storage.DocTypes.UTD && currDocType != Storage.DocTypes.InvMarks) // finded BC is not in WB yet. Inserting new position:
            {
                // update only existed rows in TH Docs. Not add new ones.
                DocumentsDataSet.MT_linesRow newLineRow = Program.storage.dsDoc.MT_lines.NewMT_linesRow();

                //newLineRow.LineID    = DBNull.Value;         // autoincrement
                newLineRow.DocName = currDoc.DocName;
                newLineRow.Pos = pos;                  // position of current row in WB
                newLineRow.GoodsID = bcInfo.gdRow.GoodsID;
                newLineRow.GoodsName = bcInfo.gdRow.Name;
                newLineRow.UnitBC = bcInfo.bcRow.UnitBC;
                newLineRow.BC = bcInfo.bcRow.BC;
                newLineRow.AlcCode = s_GTIN;
                newLineRow.PartIDTH = currDoc.DocName + "_" + s_GTIN;
                // if inc waybill then set 0 (will be filled later explicitly)
                newLineRow.Price = (currDocType == Storage.DocTypes.ExtPurchWB || currDocType == Storage.DocTypes.IntPurchWB) ?
                        0 : bcInfo.bcRow.PriceBC;
                newLineRow.FactQnty = 0;
                newLineRow.DocQnty = 0;
                newLineRow.Flags = (long) Storage.DocLineFlags.isNotEditManualQnty;
                Program.storage.dsDoc.MT_lines.Rows.Add(newLineRow);
                TALines.DirectInsertNonQuery(newLineRow); // don't write to DB until confirmed+
                this.LoadDataInit();
                TrySetCurLine(dv.Count - 1); // also selects newRow
                isMarksReg = true;
                pos++;
                // if new docline scanned - require explicit entering some fields
                if (currDocType == Storage.DocTypes.Inv && docLineEditor1.Visible)
                    docLineEditor1.btnAccept_Click(this, EventArgs.Empty); // write to db already
                foundLinesRows = currDoc.GetMT_linesRows()
                        .Where(t = > t.GoodsID == bcInfo.gdRow.GoodsID && t.UnitBC == bcInfo.bcRow.UnitBC).
                ToArray();
                ProcMarkCr(foundLinesRows[0], bc);
                GoLine(foundLinesRows[0], foundLinesRows[0].FactQnty);
                TALines.Update(slctdLine);

            } else {
                if (currDocType == Storage.DocTypes.Introduce) {
                    foundLinesRows = currDoc.GetMT_linesRows()
                            .Where(t = > t.GoodsID == bcInfo.gdRow.GoodsID && t.UnitBC == bcInfo.bcRow.UnitBC).
                    ToArray();
                } else {
                    foundLinesRows = currDoc.GetMT_linesRows()
                            .Where(t = > t.GoodsID == bcInfo.gdRow.GoodsID).ToArray();
                }
                if (foundLinesRows.Length == 0) {
                    Program.tsd.ScannerEnabled = false;
                    msgshow("Товар c GTIN - " + s_GTIN + " отсутствует в документе.");
                    Program.tsd.ScannerEnabled = true;
                    return;
                }
                ProcMarkCr(foundLinesRows[0], bc);
                GoLine(foundLinesRows[0], foundLinesRows[0].FactQnty);
                scanMarks1.RefreshWidgetData();
            }
            return;
        }

        if (bc.Trim() == String.Empty) return;

        if (!isMarksReg && bc.Trim().Length > 68) {
            Program.tsd.ScannerEnabled = false;
            msgshow("Сканирован неверный штрих-код.");
            Program.tsd.ScannerEnabled = true;
            return;
        }

        if ((bc.Trim().Length == 68 || bc.Trim().Length == 150 || isUTDDoc) && isMarksReg && !isWaitMarks) {
            isWaitMarksDoc = true;
        }

        if (isWaitMarks || isWaitMarksDoc) {
            if (!isWaitMarksDoc) {
                if (DBHelper.IsMarkExists(bc + "%", editLineRow.DocName, editLineRow.PartIDTH)) {
                    editLineRow = ProcMarkReg(bc)[0];
                    if (editLineRow == null) {
                        return;
                    }
                    GoLine(editLineRow, markRow.BoxQnty);
                    scanMarks1.RefreshWidgetData();
                    return;
                } else {
                    if (bc.Trim().Length == 68) {
                        Obj tmp = null;
                        using(MT_MarkLinesTableAdapter
                                TA = new MT_MarkLinesTableAdapter())
                        try {
                            TA.Connection = SQLConn;
                            try {
                                tmp = TA.CountByMarkReg(currDoc.DocName, editLineRow.PartIDTH);
                            } catch (SQLiteException myException) {
                                MessageBox.Show(myException.Message);
                                return;
                            }
                        } finally {
                            TA.Connection = null;
                            TA.Dispose();
                        }
                        double qnty = (tmp == DBNull.Value ? 0.0 : (double) tmp);
                        if (qnty >= editLineRow.FactQnty || !isUTDDocIntroduce) {
                            Program.tsd.ScannerEnabled = false;
                            msgshow("Марка отсутствует в документе по выбранной линии.");
                            Program.tsd.ScannerEnabled = true;
                            return;
                        }
                    } else {
                        if (!isUTDDocIntroduce) {
                            Program.tsd.ScannerEnabled = false;
                            msgshow("Марка отсутствует в документе по выбранной линии.");
                            Program.tsd.ScannerEnabled = true;
                            return;
                        } else {
                            ProcMarkCr(editLineRow, bc);
                            GoLine(editLineRow);
                            scanMarks1.RefreshWidgetData();
                            return;
                        }
                    }
                }
            } else {
                if (DBHelper.IsMarkExists(bc + "%", currDoc.DocName)) {
                    foundLinesRows = ProcMarkReg(bc);
                    // try to find the same position already in current document, grouping by goods and its unit
                    if (foundLinesRows == null || foundLinesRows.Count() != 1) {
                        return;
                    }

                    if (foundLinesRows.Length == 0) {
                        this.GetFindPage(foundLinesRows[0].PartIDTH);
                    }

                    try {
                        bcInfo = DBHelper.GetBCInfo(foundLinesRows[0].BC);
                    } catch (DBHelper.DBException e) {
                        Program.tsd.ScannerEnabled = false; // temporary switch-off scanner for preventing many blind scans
                        MessageBox.Show(e.Message + " Критическая ошибка, данные некорректны, удалите все накладные и обновите справочники.");
                        Program.tsd.ScannerEnabled = true;
                        return;
                    }

                    if (markRow != null) {
                        GoLine(foundLinesRows[0], markRow.BoxQnty);
                    } else {
                        GoLine(foundLinesRows[0], bcInfo);
                    }
                    return;
                } else {
                    if (isUTDDocIntroduce) {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Выберите линию товара на изменение.");
                        Program.tsd.ScannerEnabled = true;
                    } else {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Марка отсутствует в документе.");
                        Program.tsd.ScannerEnabled = true;
                    }
                    return;
                }
            }
        }

        // goods finded. Inserting in table now:
        // try to find the same position already in current document, grouping by goods and its unit
        try {
            bcInfo = DBHelper.GetBCInfo(bc);
        } catch (DBHelper.DBException e) {
            Program.tsd.ScannerEnabled = false; // temporary switch-off scanner for preventing many blind scans
            MessageBox.Show(e.Message + " Штрих-код не найден в справочнике товаров. Обновите справочники.");
            Program.tsd.ScannerEnabled = true;
            return;
        }

        using(DocumentsDataSet.MT_linesDataTable MT_DT = new DocumentsDataSet.MT_linesDataTable()) {
            try {
                TALines.FillByDocGdsUnit(MT_DT, currDoc.DocName, bcInfo.gdRow.GoodsID, bcInfo.bcRow.UnitBC);
            } catch (SQLiteException myException) {
                MessageBox.Show(myException.Message);
                return;
            }

            isExists = MT_DT.Count > 0;
            if (Storage.IsTHDoc(currDoc) && !isExists) {
                Program.tsd.ScannerEnabled = false;
                msgshow("Товар отсутствует в документе.");
                Program.tsd.ScannerEnabled = true;
                return;
            }
        }

        if (!isExists) // finded BC is not in WB yet. Inserting new position:
        {
            // update only existed rows in TH Docs. Not add new ones.
            DocumentsDataSet.MT_linesRow newLineRow = Program.storage.dsDoc.MT_lines.NewMT_linesRow();

            //newLineRow.LineID    = DBNull.Value;         // autoincrement
            newLineRow.DocName = currDoc.DocName;
            newLineRow.Pos = pos;                  // position of current row in WB
            newLineRow.GoodsID = bcInfo.gdRow.GoodsID;
            newLineRow.GoodsName = bcInfo.gdRow.Name;
            newLineRow.UnitBC = bcInfo.bcRow.UnitBC;
            newLineRow.BC = bcInfo.bcRow.BC;
            newLineRow.AlcCode = "";
            newLineRow.PartIDTH = "";
            // if inc waybill then set 0 (will be filled later explicitly)
            newLineRow.Price = (currDocType == Storage.DocTypes.ExtPurchWB || currDocType == Storage.DocTypes.IntPurchWB) ?
                    0 : bcInfo.bcRow.PriceBC;
            newLineRow.FactQnty = bcInfo.weight > -1 ? bcInfo.weight / 1000.0 : 1;
            newLineRow.DocQnty = 0;
            newLineRow.Flags = 0;
            Program.storage.dsDoc.MT_lines.Rows.Add(newLineRow);
            TALines.DirectInsertNonQuery(newLineRow); // don't write to DB until confirmed
            this.LoadDataInit();
            TrySetCurLine(dv.Count - 1); // also selects newRow
            pos++;
            // if new docline scanned - require explicit entering some fields
            if (currDocType == Storage.DocTypes.Inv && docLineEditor1.Visible)
                docLineEditor1.btnAccept_Click(this, EventArgs.Empty); // write to db already
            docLineEditor1.ShowWidget(slctdLine, true, TALines, isNoAddorDel);
            TALines.Update(slctdLine);

            return;
        } else // finded BC already in WB. Updating row witn increment Qnty:
        {
            foundLinesRows = currDoc.GetMT_linesRows()
                    .Where(t = > t.GoodsID == bcInfo.gdRow.GoodsID && t.UnitBC == bcInfo.bcRow.UnitBC).
            ToArray();
            if (foundLinesRows.Length == 0) {

                this.GetFindPage(bcInfo);

                foundLinesRows = currDoc.GetMT_linesRows()
                        .Where(t = > t.GoodsID == bcInfo.gdRow.GoodsID && t.UnitBC == bcInfo.bcRow.UnitBC).
                ToArray();
            }
            GoLine(foundLinesRows[0], bcInfo);
        }
    }

    private void buttonManualInput_Click(Obj sender, EventArgs e) // manInpBC
    {
        manualBCInput1.ShowWidget();
    }

    private void buttonEdit_Click(Obj sender, EventArgs e) // editCurr
    {
        if (slctdLine == null) {
            return;
        }

        if (slctdLine.RowState == System.Data.DataRowState.Detached) {
            msgshow("Выберите линию по товару для редактирования.");
            return;
        }

        if (!isMarksReg || slctdLine.AlcCode == "") {
            docLineEditor1.ShowWidget(slctdLine, false, TALines, isNoAddorDel);
        } else {
            if (scanMarks1.Visible) {
                return;
            }
            editLineRow = slctdLine;
            IsGrayZone = false;
            ParentMark = "";
            scanMarks1.ShowWidget(editLineRow, false, ((((byte) editLineRow.Flags) & ((byte) Storage.DocLineFlags.isEditManualFullQnty)) == 1), currDoc, SQLConn, IsGrayZone, this);
        }
    }

    private void buttonSave_Click(Obj sender, EventArgs e) // exit and save without sending
    {
        CloseWidget();
    }

    private void buttonFinish_Click(Obj sender, EventArgs e) // exit and send document
    {
        currDoc.Complete = true;
        using(MT_documentsTableAdapter TA = new MT_documentsTableAdapter(Program.storage.ConStrDoc)) {
            TA.Update(currDoc);
        }
        string Msg = "Документ помечен для отправки.";

        // try to send document immideately
        MessageBox.Show(Msg);
        CloseWidget();
    }

    private void docLineEditor1_WidgetOpening(Obj sender, EventArgs e) {
        SetModal(true);
        if (currDocType != Storage.DocTypes.Inv)
            Program.tsd.ScannerEnabled = false;
    }

    private void docLineEditor1_WidgetClosed(Obj sender, EventArgs e) {
        SetModal(false);
        Program.tsd.ScannerEnabled = true;
    }

    private void docLineEditor1_LineDeleted(Obj sender, EventArgs e) {
        this.LoadDataInit();
        RefreshSum();
        TrySetCurLine(0);
    }

    private void docLineEditor1_LineEdited(Obj sender, LineEditedEventArgs e) {
        RefreshSum();
    }

    private void scanMarks1_WidgetClosed(Obj sender, EventArgs e) {
        isWaitMarks = false;
    }

    private void scanMarks1_WidgetOpening(Obj sender, EventArgs e) {
        isWaitMarks = true;
    }

    private void manualBCInput1_WidgetOpening(Obj sender, EventArgs e) {
        SetModal(true);
    }

    private void manualBCInput1_WidgetClosed(Obj sender, EventArgs e) {
        SetModal(false);
        manualBCInput1.HideBtn = false;
        manualBCInput1.Option = "Введите штрих-код";
        //alcCode = "";
    }

    private void manualBCInput1_InputAccepted(Obj o, InputEventArgs e) {
        BC_processing(e.inputStr);
    }

    // hack for .net cf (replacement for "DataGrid.SelectionMode = FullRowMode" option)
    private void dataGrid1_CurrentCellChanged(Obj sender, EventArgs e) {
        slctdLine = dv[dataGrid1.CurrentRowIndex].Row as DocumentsDataSet.MT_linesRow;
        dataGrid1.SelectOnlyRow(dataGrid1.CurrentRowIndex); // select whole line instead of cell
    }

    private void docLineEditor1_Click(Obj sender, EventArgs e) {
    }

    private void msgshow(string arg) {
        MessageBox.Show(arg);
    }

    private void lblTotalLabel_ParentChanged(Obj sender, EventArgs e) {
    }

    private void PageUpDown_ValueChanged(Obj sender, EventArgs e) {
        this.LoadData();
    }

    private void GetFindPage(DBHelper.BCInfo bcInfo) {
        TALines.FillByDocGdsUnit(Program.storage.dsDoc.MT_lines, currDoc.DocName, bcInfo.gdRow.GoodsID, bcInfo.bcRow.UnitBC);
        if (Program.storage.dsDoc.MT_lines.Count == 0) {
            return;
        }
        GetFindPage(Program.storage.dsDoc.MT_lines);
    }

    private void GetFindPage(string PartIDTH) {
        TALines.FillByPartIDTH(Program.storage.dsDoc.MT_lines, currDoc.DocName, PartIDTH);
        GetFindPage(Program.storage.dsDoc.MT_lines);
    }

    private void GoLine(DocumentsDataSet.MT_linesRow editLineRow) {
        double newFactQnty = editLineRow.FactQnty + 1;
        GoLine(editLineRow, newFactQnty);
    }

    private void GoLine(DocumentsDataSet.MT_linesRow editLineRow, long qnty) {
        double newFactQnty = editLineRow.FactQnty + qnty;
        GoLine(editLineRow, newFactQnty);
    }

    private void GoLine(DocumentsDataSet.MT_linesRow editLineRow, double newFactQnty) {
        if (Storage.IsSalesWB(currDocType) &&
                newFactQnty > editLineRow.DocQnty) {
            Program.tsd.ScannerEnabled = false;
            msgshow("Фактическое количество не должно быть больше документарного.");
            Program.tsd.ScannerEnabled = true;
            return;
        } else editLineRow.FactQnty = newFactQnty;
        try {
            TALines.Update(editLineRow);
        } catch (SQLiteException myException) {
            MessageBox.Show(myException.Message);
            return;
        }

        if (docLineEditor1.Visible) {
            docLineEditor1.RefreshWidgetData();
        }
        RefreshSum();
        dataGrid1.ForceCurrentCellChangedEvent(
                dv.Find(new Obj[]{editLineRow.DocName, editLineRow.Pos})
        ); // focus on just affected row
    }

    private void GoLine(DocumentsDataSet.MT_linesRow editLineRow, DBHelper.BCInfo bcInfo) {
        double newFactQnty = bcInfo.weight == -1 ?
                editLineRow.FactQnty + 1 : // not weighted
                editLineRow.FactQnty + bcInfo.weight / 1000.0; // weighted

        GoLine(editLineRow);
    }

    private void ProcMarkCr(DocumentsDataSet.MT_linesRow editLineRow, string bc) {

        if (DBHelper.IsMarkExists(bc + "%", currDoc.DocName)) {
            if (IsGrayZone) {
                DocumentsDataSet.MT_MarkLinesRow markRow =
                        Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
                using(MT_MarkLinesTableAdapter TA =
                        new MT_MarkLinesTableAdapter())
                try {
                    TA.Connection = SQLConn;

                    markRow = Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
                    markRow = TA.GetDataByMark(bc + "%", currDoc.DocName)[0];

                    if (markRow.MarkParent != ParentMark) {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Пачка принадлежит другому блоку.");
                        Program.tsd.ScannerEnabled = true;
                        return;
                    }

                    if (markRow.BoxQnty != 1) {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Сканируйте пачку блока.");
                        Program.tsd.ScannerEnabled = true;
                        return;
                    }

                    if (markRow.Sts == "TSD" || markRow.Sts == "CrTSD" || markRow.Sts == "Check" || markRow.Sts == "CheckScan") {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Марка уже просканирована.");
                        Program.tsd.ScannerEnabled = true;
                        return;
                    }

                    markRow.Sts = "CheckScan";
                    markRow.MarkParent = ParentMark;
                    try {
                        TA.Update(markRow);
                    } catch (SQLiteException myException) {
                        Program.tsd.ScannerEnabled = false;
                        MessageBox.Show(myException.Message);
                        Program.tsd.ScannerEnabled = true;
                        return;
                    }
                    editLineRow.FactQnty = editLineRow.FactQnty + markRow.BoxQnty;
                    TALines.Update(editLineRow);
                    scanMarks1.textBoxWithPromptFact.Text = (Convert.ToInt32(scanMarks1.textBoxWithPromptFact.Text) + 1).ToString();

                    return;
                } finally {
                    TA.Connection = null;
                    TA.Dispose();
                }

            } else {
                Program.tsd.ScannerEnabled = false;
                msgshow("Марка уже присутствует в документе.");
                Program.tsd.ScannerEnabled = true;
            }
            return;
        }

        DocumentsDataSet.MT_MarkLinesRow newMarkRow =
                Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
        using(MT_MarkLinesTableAdapter TA =
                new MT_MarkLinesTableAdapter())
        try {
            TA.Connection = SQLConn;

            try {
                Obj tmp = TA.CountMarkChild(ParentMark, currDoc.DocName);
                double qntyMC = (tmp == DBNull.Value ? 0.0 : (long) tmp);
                if (qntyMC >= 10) {
                    Program.tsd.ScannerEnabled = false;
                    msgshow("Невозможно распределить пачку в блоке.");
                    Program.tsd.ScannerEnabled = true;
                    return;
                }
            } catch (SQLiteException myException) {
                MessageBox.Show(myException.Message);
                return;
            }

            newMarkRow.DocName = currDoc.DocName;
            newMarkRow.LineID = editLineRow.LineID;
            newMarkRow.MarkCode = bc;
            newMarkRow.PartIDTH = editLineRow.PartIDTH;
            newMarkRow.Sts = "CrTSD";
            if (ParentMark == null) {
                newMarkRow.MarkParent = "";
            } else {
                newMarkRow.MarkParent = ParentMark;
            }
            string vMarkShort = "";
            vMarkShort = Storage.GetCodeIdent(bc);
            if (vMarkShort.Length > 21) {
                if (IsGrayZone) {
                    Program.tsd.ScannerEnabled = false;
                    msgshow("Сканируйте пачку блока.");
                    Program.tsd.ScannerEnabled = true;
                    return;
                }
                newMarkRow.BoxQnty = 10;
            } else {
                newMarkRow.BoxQnty = 1;
            }
            try {
                TA.DirectInsertNonQuery(newMarkRow);
            } catch //(SQLiteException myException)
            {
                Program.tsd.ScannerEnabled = false;
                //MessageBox.Show(myException.Message);
                msgshow("Марка уже просканирована.");
                Program.tsd.ScannerEnabled = true;
                return;
            }

            editLineRow.FactQnty = editLineRow.FactQnty + newMarkRow.BoxQnty;
            TALines.Update(editLineRow);
            if (IsGrayZone)
                scanMarks1.textBoxWithPromptFact.Text = (Convert.ToInt32(scanMarks1.textBoxWithPromptFact.Text) + 1).ToString();
        } finally {
            TA.Connection = null;
            TA.Dispose();
        }
    }

    private DocumentsDataSet.MT_linesRow[] ProcMarkReg(string bc) {
        DocumentsDataSet.MT_linesRow[] foundLinesRowsPrMR = null;

        using(MT_MarkLinesTableAdapter TA = new MT_MarkLinesTableAdapter())
        try {
            TA.Connection = SQLConn;
            markRow = Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
            markRow = TA.GetDataByMark(bc + "%", currDoc.DocName)[0];

            foundLinesRowsPrMR = currDoc.GetMT_linesRows().Where(t = > t.PartIDTH == markRow.PartIDTH).
            ToArray();

            if (currDocType == Storage.DocTypes.UTD && markRow.BoxQnty == 1) {
                Program.tsd.ScannerEnabled = false;
                msgshow("Сканируйте упаковку.");
                Program.tsd.ScannerEnabled = true;
                return null;
            }

            if (markRow.Sts == "TSD" || markRow.Sts == "CrTSD" || markRow.Sts == "Check" || markRow.Sts == "CheckScan") {
                Program.tsd.ScannerEnabled = false;
                msgshow("Марка уже просканирована.");
                Program.tsd.ScannerEnabled = true;
                return null;
            }

            if (markRow.Sts == "Ungrouped") {
                Program.tsd.ScannerEnabled = false;
                msgshow("Упаковка разгруппирована.");
                Program.tsd.ScannerEnabled = true;
                return null;
            }

            if (markRow.Sts == "NotCorrect") {
                Program.tsd.ScannerEnabled = false;
                msgshow("Товар не подлежит приемке, т.к. не прошел проверку на корректность.");
                Program.tsd.ScannerEnabled = true;
                return null;
            }

            if (markRow.Sts == "GrayZone") {
                if (scanMarks1.Visible) {
                    if (bc.Length <= 21) {
                        Program.tsd.ScannerEnabled = false;
                        msgshow("Маркировка пачки серой зоны. Вначале сканируйте блок.");
                        Program.tsd.ScannerEnabled = true;
                        return null;
                    }

                    Program.tsd.ScannerEnabled = false;
                    msgshow("Маркировка серой зоны. Сканируйте в режиме редактирования документа.");
                    Program.tsd.ScannerEnabled = true;
                    return null;
                }

                IsGrayZone = true;
                ParentMark = markRow.MarkCode;
                scanMarks1.ShowWidget(foundLinesRowsPrMR[0], false, ((((byte) foundLinesRowsPrMR[0].Flags) & ((byte) Storage.DocLineFlags.isEditManualFullQnty)) == 1), currDoc, SQLConn, IsGrayZone, this);

                //Program.tsd.ScannerEnabled = false;
                //msgshow("Марка серой зоны, продолжить приемку упаковки нужно в ТН.");
                //Program.tsd.ScannerEnabled = true;
                return null;
            }

            if (markRow.Sts == "UTD") {
                string markCode;
                markCode = markRow.MarkCode;
                Cursor.Current = Cursors.WaitCursor;
                using(SQLiteCommand cmd = new SQLiteCommand(SQLConn)) {
                    cmd.CommandText = "UPDATE MT_MarkLines SET Sts = @sts WHERE MarkParent LIKE @markCode and DocName = @DocName";

                    cmd.Parameters.AddWithValue("@sts", "Check");
                    cmd.Parameters.AddWithValue("@markCode", markCode + "%");
                    cmd.Parameters.AddWithValue("@DocName", markRow.DocName);

                    try {
                        cmd.Prepare();
                    } catch (SQLiteException myException) {
                        MessageBox.Show("Ошибка выполения комманды 14. " + myException.Message + " " + SQLConn.ConnectionString);
                    }
                    ;
                    //SQLConn.Open();
                    try {
                        cmd.ExecuteNonQuery();
                    } catch (SQLiteException myException) {
                        MessageBox.Show("Ошибка выполения комманды 14. " + myException.Message + " " + SQLConn.ConnectionString);
                    }
                }
                markRow.Sts = "CheckScan";

                if (markRow.MarkParent != "") {
                    using(SQLiteCommand cmd = new SQLiteCommand(SQLConn)) {
                        cmd.CommandText = "UPDATE MT_MarkLines SET Sts = @sts WHERE MT_MarkLines.MarkCode LIKE @MarkParent and DocName = @DocName";

                        cmd.Parameters.AddWithValue("@sts", "Ungrouped");
                        cmd.Parameters.AddWithValue("@MarkParent", markRow.MarkParent + "%");
                        cmd.Parameters.AddWithValue("@DocName", markRow.DocName);

                        try {
                            cmd.Prepare();
                        } catch (SQLiteException myException) {
                            MessageBox.Show("Ошибка выполения комманды 14. " + myException.Message + " " + SQLConn.ConnectionString);
                        }
                        ;
                        //SQLConn.Open();

                        try {
                            cmd.ExecuteNonQuery();
                        } catch (SQLiteException myException) {
                            MessageBox.Show("Ошибка выполения комманды 15. " + myException.Message + " " + SQLConn.ConnectionString);
                        }
                    }
                }
                Cursor.Current = Cursors.Default;
                //markRow.Sts = "Check";
            } else {
                markRow.Sts = "TSD";
            }

            try {
                TA.UpdateSts(markRow.Sts, markRow.LineID, markRow.MarkCode);
            } catch (SQLiteException myException) {
                MessageBox.Show(myException.Message);
            }
        } finally {
            TA.Connection = null;
            TA.Dispose();
        }
        return foundLinesRowsPrMR;
    }

    private void GetFindPage(DocumentsDataSet.MT_linesDataTable MT_DT) {
        int findnumpage = ((int) MT_DT[0].LineID - LineIdBegin) / Program.cfg.N_Pages + 1;
        if (PageUpDown.Value != findnumpage) {
            PageUpDown.Value = findnumpage;
        } else {
            LoadData();
        }
    }
}
