package com.expertek.tradehouse.components;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.R;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.Barcode;
import com.expertek.tradehouse.dictionaries.entity.Good;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BarcodeProcessor implements Parcelable {
    public static final int ERROR_VALUE = -2;
    public static final int SINGLETON_LIST = -1;
    private static final List<String> scannedstat = Arrays.asList("TSD", "CrTSD", "Check", "CheckScan");
    private final DbDictionaries dbc = Application.dictionaries.db();
    private final DBDocuments dbd = Application.documents.db();
    private long markCounter;
    private long lineCounter;
    public final MarkList marklines;
    public final List<Line> lines;
    public final Document document;
    private Line line = null;

    /**
     * PagingList with capability of delta updates
     */
    private static class MarkList extends PagingList<Markline> {
        private final SparseBooleanArray setter = new SparseBooleanArray(20);

        public MarkList() {
            super();
        }

        public MarkList(@NonNull DBDocuments dbd, @NonNull Document document, @Nullable Line line) {
            super(dbd.marklines().load(document.DocName, (line != null ? line.PartIDTH : null)));
        }

        @Override
        public Markline set(int position, Markline element) {
            setter.put(position, true);
            return super.set(position, element);
        }

        @Override
        public boolean add(Markline element) {
            setter.put(size(), true);
            return super.add(element);
        }

        protected void acceptModified() {
            setter.clear();
        }

        protected Markline[] getModified() {
            final Markline[] updates = (Markline[]) Array.newInstance(Markline.class, setter.size());
            for (int i = 0, idx = 0; i < update.size(); i++) {
                if (setter.get(update.keyAt(i))) updates[idx++] = update.valueAt(i);
            }
            return updates;
        }
    }

    public BarcodeProcessor(@NonNull Document document, @NonNull List<Line> lines) {
        this.markCounter = dbd.marklines().getNextId();
        this.lineCounter = dbd.lines().getNextId();
        this.document = document;
        this.lines = lines;

        if (MainSettings.CheckMarks && document.isMarkline()) {
            this.marklines = new MarkList(dbd, document, line);
        } else {
            this.marklines = new MarkList();
        }
    }

    public void apply(BarcodeProcessor processor) {
        assert (this.document.DocName.equals(processor.document.DocName));
        this.markCounter = processor.markCounter;
        this.line = processor.line;
        this.marklines.addAll(processor.marklines);
    }

    protected BarcodeProcessor(Parcel in) {
        this.markCounter = in.readLong();
        this.lineCounter = SINGLETON_LIST;
        this.document = (Document) in.readSerializable();
        this.line = (Line) in.readSerializable();
        this.lines = Collections.singletonList(this.line);
        this.marklines = new MarkList(dbd, document, line);
        in.readList(this.marklines, Markline.class.getClassLoader());
        this.marklines.acceptModified();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(markCounter);
        dest.writeSerializable(document);
        dest.writeSerializable(line);
        dest.writeList(filterSelection(lineCounter != SINGLETON_LIST));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BarcodeProcessor> CREATOR = new Creator<BarcodeProcessor>() {
        @Override
        public BarcodeProcessor createFromParcel(Parcel in) {
            return new BarcodeProcessor(in);
        }

        @Override
        public BarcodeProcessor[] newArray(int size) {
            return new BarcodeProcessor[size];
        }
    };

    // Line <-> Markline correspondence
    private final Predicate<Markline> linePredicate = new Predicate<Markline>() {
        @Override
        public boolean test(Markline markline) {
            return !markline.PartIDTH.equals(line.PartIDTH);
        }
    };

    public boolean isMarked() {
        assert (line != null);
        if (line.PartIDTH == null) return false;
        return marklines.stream().anyMatch(linePredicate);
    }

    private List<Markline> filterSelection(boolean filtered) {
        assert (line != null);
        final List<Markline> result = Arrays.asList(marklines.getModified());
        if (filtered && (line.PartIDTH != null)) result.removeIf(linePredicate);
        return result;
    }

    private int findLine(BarcodeMarker marker) {
        final String partIDTH = getPartIDTH(marker);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).PartIDTH.equals(partIDTH)) return i;
        }
        return SINGLETON_LIST;
    }

    private Markline findMark(BarcodeMarker marker) {
        for (int i = 0; i < marklines.size(); i++) {
            if (marklines.get(i).MarkCode.equals(marker.scanned))
                return marklines.get(i);
        }
        return null;
    }

    private String getPartIDTH(BarcodeMarker marker) {
        return (document.DocName + "_" + marker.gtin);
    }

    private long getMarkId() {
        assert (markCounter >= 0);
        return markCounter++;
    }

    private long getLineId() {
        assert (lineCounter >= 0);
        return lineCounter++;
    }

    private int getLinePos() {
        if (lines.isEmpty()) return 1;
        return lines.get(lines.size() - 1).Pos + 1;
    }

    public Line getLine() {
        return line;
    }

    public int setLine(Line line) {
        this.line = line;
        final int position = lines.indexOf(line);
        final double delta;

        if (position < 0) {
            delta = line.FactQnty;
            lines.add(line);
        } else {
            delta = line.FactQnty - lines.get(position).FactQnty;
            lines.set(position, line);
        }
        correctDocSumm(line, delta);
        return position;
    }

    private void correctDocSumm(Line line, double quantity) {
        document.FactSum += quantity * line.Price;
    }

    public Line createLine() {
        line = new Line();
        line.DocName = document.DocName;
        line.LineID = (int) getLineId();
        line.Pos = getLinePos();
        line.PartIDTH = null;
        line.Flags = 0;
        return line;
    }

    public boolean updateLine(@NonNull Context context, @NonNull BarcodeMarker marker) {
        assert (line != null);

        if (!marker.isWellformed()) {
            Dialogue.Error(context, R.string.msg_bar_incorrect);
            return false;
        }

        final Barcode barcode = dbc.barcodes().get(marker.gtin);
        if (barcode == null) {
            Dialogue.Error(context, R.string.msg_bar_not_found);
            return false;
        }

        final Good good = dbc.goods().get(barcode.GoodsID);
        if (good == null) {
            Dialogue.Error(context, R.string.msg_bar_not_found);
            return false;
        }

        line.GoodsName = good.Name;
        line.GoodsID = barcode.GoodsID;
        line.BC = barcode.BC;
        line.UnitBC = barcode.UnitBC;
        line.Price = barcode.PriceBC;
        line.DocQnty = marker.weight;
        line.FactQnty = marker.weight;
        line.AlcCode = marker.gtin;
        line.PartIDTH = getPartIDTH(marker);
        return true;
    }

    public int add(@NonNull Context context, @NonNull BarcodeMarker marker) {
        final Markline markline = findMark(marker);
        if (!checkMarkline(context, markline, false)) return ERROR_VALUE;

        if ((lineCounter == SINGLETON_LIST) && (line.GoodsID <= 0)) {
            if (!updateLine(context, marker)) return ERROR_VALUE;
        }

        final int position = findLine(marker);
        if (position < 0) {
            if (lineCounter == SINGLETON_LIST) {
                Dialogue.Error(context, R.string.msg_bar_line_wrong);
                return ERROR_VALUE;
            } else if (document.isReadonly() || document.isTreadHouse()) {
                Dialogue.Error(context, R.string.msg_good_absent);
                return ERROR_VALUE;
            }

            line = createLine();
            if (!updateLine(context, marker)) return ERROR_VALUE;
        } else {
            line = lines.get(position);
            line.DocQnty += marker.weight;
            line.FactQnty += marker.weight;
            correctDocSumm(line, marker.weight);
        }

        return position;
    }

    private boolean checkMarkline(Context context, Markline markline, boolean required) {
        if (required && (markline == null)) {
            Dialogue.Error(context, R.string.msg_mark_absent);
        } else if (markline == null) {
            return true;
        } else if (document.DocType.equals(Document.UTD) && (markline.BoxQnty == 1)) {
            Dialogue.Error(context, R.string.msg_scan_pack);
        } else if (scannedstat.contains(markline.Sts)) {
            Dialogue.Error(context, R.string.msg_already_scanned);
        } else if ("Ungrouped".equals(markline.Sts)) {
            Dialogue.Error(context, R.string.msg_ungroupped);
        } else if ("NotCorrect".equals(markline.Sts)) {
            Dialogue.Error(context, R.string.msg_not_allowed);
        } else if ("GrayZone".equals(markline.Sts)) {
        } else if ("UTD".equals(markline.Sts)) {
        } else {
            return true;
        }
        return false;
    }
}
