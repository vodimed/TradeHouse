package com.obsolete;

import android.database.sqlite.SQLiteException;
import android.graphics.Point;

import com.expertek.tradehouse.dictionaries.entity.object;

public class ScanMarks {
    public event EventHandler
    WidgetOpening = null;
    public event LineEditedEventHandler
    LineEdited = null;
    public event EventHandler
    WidgetClosed = null;
    public DocumentsDataSet.MT_linesRow EditedLineq
    public bool isShown = false;
    public bool IsInitialEdit;
    //public event CancelEventHandler Cancel = null;
    private DocumentsDataSet.MT_documentsRow currDoc = null;
    private DocumentsDataSet.MT_linesRow editedLine = null;
    private DocumentsDataSet.MT_MarkLinesRow markRow = null;
    private SQLiteConnection SQLConn;
    private SQLiteTransaction TAMarksLinesTrans;
    private MT.DocEditor DEditor;
    private MT_MarkLinesTableAdapter TAMarksLines;
    private bool initialEdit = false; // REQUIRE entering?

    {
        get { return editedLine; }
        set {
            editedLine = value;
            textBoxWithPromptPrice.Enabled = false;
            textBoxWithPromptDoc.Enabled = false;
            textBoxWithPromptFact.Enabled = false;
        }
    }

    {
        get { return initialEdit; }
        set { initialEdit = value; }
    }

    public ScanMarks() {
        InitializeComponent();
        this.Visible = false;
    }

    // TODO: make all write-to-DB related actions in handlers in parent widget!
    // not make private functions public!
    public void btnAccept_Click(object sender, EventArgs e) {
        double newPrice = 0;
        double newFactQnty = 0;
        double newDocQnty = 0;
        try {
            newFactQnty = double.Parse(textBoxWithPromptFact.Text);
            newDocQnty = double.Parse(textBoxWithPromptDoc.Text);
            newPrice = double.Parse(textBoxWithPromptPrice.Text);
            if (DEditor.IsGrayZone && newFactQnty < 10) {
                MessageBox.Show("Допустимо только полное сканирование блока.");
                return;
            }
        } catch (Exception) {
            MessageBox.Show("Некорректный формат введенных данных.");
            return;
        }

        this.Visible = false;

        // else update line
        if (accepetallqnty.CheckState == CheckState.Checked) {
            newFactQnty = editedLine.DocQnty;
        }

        if (accepetallqnty.CheckState == CheckState.Checked) {
            try {
                int i = 0;
                while (i < Program.storage.dsDoc.MT_MarkLines.Count) {
                    markRow = Program.storage.dsDoc.MT_MarkLines.ToArray()[i];
                    markRow.Sts = "TSD";
                    try {
                        TAMarksLines.UpdateSts(markRow.Sts, markRow.LineID, markRow.MarkCode);
                    } catch (SQLiteException myException) {
                        MessageBox.Show(myException.Message);
                    }
                    i++;
                }
            } catch (Exception) {
                MessageBox.Show("Ошибка записи.");
                return;
            } finally {
                TAMarksLines.Connection = null;
                TAMarksLines.Dispose();
            }
            accepetallqnty.CheckState = CheckState.Unchecked;
        }
        TAMarksLinesTrans.Commit();
        TAMarksLines.Connection = null;
        TAMarksLinesTrans = null;
        TAMarksLines.Dispose();
        DEditor.IsGrayZone = false;
        DEditor.ParentMark = "";

        if (!Storage.IsTHDoc(editedLine.MT_documentsRow)) {
            this.editedLine.Price = newPrice;
            this.editedLine.DocQnty = newDocQnty;
        }
        this.editedLine.FactQnty = newFactQnty;

        using(MT_linesTableAdapter TA = new MT_linesTableAdapter(Program.storage.ConStrDoc)) {
            TA.Update(this.editedLine);
        }

        if (LineEdited != null) LineEdited(this, new LineEditedEventArgs(this.editedLine));
        if (WidgetClosed != null) WidgetClosed(this, EventArgs.Empty);
    }

    private void btnCancel_Click(object sender, EventArgs e) {
        TAMarksLinesTrans.Rollback();
        TAMarksLines.Connection = null;
        TAMarksLinesTrans = null;
        TAMarksLines.Dispose();
        DEditor.IsGrayZone = false;
        DEditor.LoadDataInit();
        this.Visible = false;

        if (WidgetClosed != null) WidgetClosed(this, EventArgs.Empty);
    }

    public void ShowWidget(
            DocumentsDataSet.MT_linesRow line
            , bool isInitial
            , bool isManualFullQnty
            , DocumentsDataSet.MT_documentsRow incurrDoc
            , SQLiteConnection inSQLConn
            , bool isGrayZone
            , MT.DocEditor DE
    ) {
        textBoxWithPromptFact.Text = "0";
        DEditor = DE;
        this.EditedLine = line;
        this.IsInitialEdit = isInitial;
        currDoc = incurrDoc;
        SQLConn = inSQLConn;
        accepetallqnty.CheckState = CheckState.Unchecked;
        if (isManualFullQnty && editedLine.DocQnty != editedLine.FactQnty) {
            accepetallqnty.Enabled = true;
        } else {
            accepetallqnty.Enabled = false;
        }
        try {
            TAMarksLines = new MT_MarkLinesTableAdapter();
            TAMarksLines.Connection = SQLConn;
            try {
                TAMarksLinesTrans = TAMarksLines.Connection.BeginTransaction();
            } catch (SQLiteException myException) {
                MessageBox.Show("Ошибка ScanMarks. " + myException.Message);
                return;
            }
            markRow = Program.storage.dsDoc.MT_MarkLines.NewMT_MarkLinesRow();
            TAMarksLines.FillByPartID(Program.storage.dsDoc.MT_MarkLines, currDoc.DocName, editedLine.PartIDTH);
        } catch (Exception) {
            MessageBox.Show("Ошибка ScanMarks. ");
            return;
        }

        if (isGrayZone) {
            this.label1.Text = "Сканируйте пачки блока:";
            this.btnCancel.Visible = true;
            lblDoc.Visible = true;
            this.accepetallqnty.Visible = false;
            //textBoxWithPromptDoc.Visible = false;
            lblDoc.Text = "Состав МОТП:";
            lblFact.Text = "Пачки:";
        } else {
            this.label1.Text = "Сканируйте марки:";
            this.btnCancel.Visible = false;
            this.accepetallqnty.Visible = true;
            textBoxWithPromptFact.Visible = true;
            lblFact.Text = "Факт. кол-во:";
            lblDoc.Visible = true;
        }
        ShowWidget();
    }

    private void ShowWidget() {
        if (WidgetOpening != null) WidgetOpening(this, EventArgs.Empty);

        const int widgetWidth = 200, widgetHeight = 175;

        Rectangle parentRect = this.Parent.ClientRectangle;
        this.Location = new Point(
                (parentRect.Size.Width - widgetWidth) / 2,
                parentRect.Top + 10
        );
        this.Size = new Size(widgetWidth, widgetHeight); // size will be constant

        RefreshWidgetData();

        // make textBox ready for editing
        if (Storage.IsTHDoc(editedLine.MT_documentsRow)) // TH docs
            textBoxWithPromptFact.Focus();
        else // new WBs
        {
            if (editedLine.MT_documentsRow.DocType == Storage.DocTypes.IntSalesWB) { // New IntSalesWB
                if (editedLine.DocQnty == 0)
                    textBoxWithPromptDoc.Focus();
                else
                    textBoxWithPromptFact.Focus();
            } else
                textBoxWithPromptPrice.Focus();
        }

        this.isShown = true;
        this.BringToFront();
        this.Visible = true;

        if (DEditor.IsGrayZone) {
            try {
                object tmp = TAMarksLines.CountMarkChild(DEditor.ParentMark, currDoc.DocName);
                double qntyMC = (tmp == DBNull.Value ? 0.0 : (long) tmp);
                textBoxWithPromptDoc.Text = qntyMC.ToString();
            } catch (SQLiteException myException) {
                MessageBox.Show(myException.Message);
                return;
            }
        }
    }

    public void CloseWidget() {
        this.isShown = false;
        this.Visible = false;
        DEditor.IsGrayZone = false;
        if (WidgetClosed != null) WidgetClosed(this, EventArgs.Empty);
    }

    public void RefreshWidgetData() {
        lblGoodsName.Text = this.editedLine.GoodsName;
        textBoxWithPromptPrice.Text = this.editedLine.Price.ToString("N2");

        if (!DEditor.IsGrayZone) {
            textBoxWithPromptFact.Text = this.editedLine.FactQnty.ToString();
        }

        if (!DEditor.IsGrayZone) {
            textBoxWithPromptDoc.Text = this.editedLine.DocQnty.ToString();
        }
    }
}
