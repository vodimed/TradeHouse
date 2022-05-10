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
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BarcodeProcessor implements Parcelable {
    public static final int ERROR_VALUE = -2;
    public static final int SINGLETON_LIST = -1;
    private final DbDictionaries dbc = Application.dictionaries.db();
    private final DBDocuments dbd = Application.documents.db();
    private long markCounter;
    private long lineCounter;
    public final MarkList marklines;
    public final List<Line> lines;
    public final Document document;
    public Markline parentmark = null;
    public Line line = null;

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
                if (setter.get(update.keyAt(i))) {
                    updates[idx++] = update.valueAt(i);
                }
            }
            return updates;
        }
    }

    public BarcodeProcessor(@NonNull Document document, @NonNull List<Line> lines) {
        this.markCounter = dbd.marklines().getNextId();
        this.lineCounter = dbd.lines().getNextId();
        this.document = document;
        this.lines = lines;

        if (document.isMarked()) {
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
        this.parentmark = (Markline) in.readSerializable();
        this.line = (Line) in.readSerializable();
        this.lines = Collections.singletonList(this.line);
        this.marklines = new MarkList(dbd, document, line);
        in.readList(this.marklines, Markline.class.getClassLoader());
        this.marklines.acceptModified();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (line == null) createLine();
        dest.writeLong(markCounter);
        dest.writeSerializable(document);
        dest.writeSerializable(parentmark);
        dest.writeSerializable(line);
        dest.writeList(filterModified(!isModeSingle()));
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
            return markline.PartIDTH.equals(line.PartIDTH);
        }
    };

    private static final Predicate<Markline> markParent = new Predicate<Markline>() {
        @Override
        public boolean test(Markline markline) {
            return markline.isParent();
        }
    };

    private List<Markline> filterModified(boolean filtered) {
        final Markline[] modified = marklines.getModified();

        if (!filtered || (line.PartIDTH == null)) {
            return Arrays.asList(modified);
        } else {
            return Arrays.stream(modified).filter(linePredicate).collect(Collectors.toList());
        }
    }

    private boolean isModeSingle() {
        return (lineCounter == SINGLETON_LIST);
    }

    public boolean isLineMarked() {
        if ((line == null) || (line.PartIDTH == null)) return false;
        return marklines.stream().anyMatch(linePredicate);
    }

    private Markline getLineMarker(@Nullable Markline parentmark, @Nullable Markline markline) {
        if ((line == null) || (line.PartIDTH == null)) {
            return null;
        } else if ((markline != null) && linePredicate.test(markline) && markline.isParent()) {
            return markline;
        } else if ((parentmark != null) && linePredicate.test(parentmark) && parentmark.isParent()) {
            return parentmark;
        } else try {
            return marklines.stream().filter(linePredicate).filter(markParent).limit(1).iterator().next();
        } catch (Exception e) {
            return null;
        }
    }

    private int findLine(BarcodeMarker marker) {
        final String partIDTH = getPartIDTH(marker);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).PartIDTH.equals(partIDTH)) return i;
        }
        return SINGLETON_LIST;
    }

    private int findMark(BarcodeMarker marker) {
        return findMark(marker.scanned);
    }

    private int findMark(String scanned) {
        if (scanned != null) {
            if ((parentmark != null) && scanned.equals(parentmark.MarkCode)) {
                return marklines.indexOf(parentmark);
            } else for (int i = 0; i < marklines.size(); i++) {
                if (marklines.get(i).MarkCode.equals(scanned))
                    return i;
            }
        }
        return SINGLETON_LIST;
    }

    private String getPartIDTH(BarcodeMarker marker) {
        return (document.DocName + "_" + marker.gtin);
    }

    private void correctDocSumm(Line line, double quantity) {
        document.FactSum += quantity * line.Price;
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

    private void createLine() {
        line = new Line();
        line.DocName = document.DocName;
        line.LineID = (int) getLineId();
        line.Pos = getLinePos();
        line.PartIDTH = null;
        line.Flags = 0;
    }

    public void selectLine(int position, @Nullable Markline markline) {
        if ((position >= 0) && (position < lines.size())) {
            line = lines.get(position);
            parentmark = getLineMarker(parentmark, markline);
        } else if (!isModeSingle()) {
            line = null;
            parentmark = null;
        }
    }

    private boolean checkLine(@NonNull Context context, @NonNull BarcodeMarker marker, boolean match) {
        if (!marker.isWellformed()) {
            Dialogue.Error(context, R.string.msg_bar_incorrect);
            return false;
        } else if (match && (parentmark != null) && (line.FactQnty + marker.weight > parentmark.BoxQnty)) {
            Dialogue.Error(context, R.string.msg_pack_unable);
            return false;
        } else if (!match && isLineMarked()) {
            Dialogue.Error(context, R.string.msg_mark_line_wrong);
            return false;
        } else if (!match && (document.isReadonly() || document.isTreadHouse())) {
            Dialogue.Error(context, R.string.msg_good_absent);
            return false;
        }
        return true;
    }

    public boolean updateLine(@NonNull Context context, @NonNull BarcodeMarker marker) {
        final Barcode barcode = dbc.barcodes().get(marker.bc);
        if (barcode == null) {
            Dialogue.Error(context, R.string.msg_bar_not_found);
            return false;
        }

        final String goodName = dbc.goods().getName(barcode.GoodsID);
        if (goodName == null) {
            Dialogue.Error(context, R.string.msg_bar_not_found);
            return false;
        }

        if (line == null) createLine();
        line.GoodsName = goodName;
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

    public int acceptLine() {
        int position = lines.indexOf(line);
        final double delta;

        if (position < 0) {
            position = lines.size();
            delta = line.FactQnty;
            lines.add(line);
        } else {
            delta = line.FactQnty - lines.get(position).FactQnty;
            lines.set(position, line);
        }
        correctDocSumm(line, delta);
        return position;
    }

    private boolean checkMarkline(@NonNull Context context, @Nullable Markline markline, boolean required) {
        if (required && (markline == null)) {
            Dialogue.Error(context, isModeSingle() ?
                    R.string.msg_mark_line_absent :
                    R.string.msg_mark_absent);
            return false;
        } else if (markline == null) {
            return true;
        } else if (!required) {
            Dialogue.Error(context, R.string.msg_already_accepted);
            return false;
        } else if (Document.UTD.equals(document.DocType) && (markline.BoxQnty <= 1)) {
            Dialogue.Error(context, R.string.msg_scan_pack);
            return false;
        } else if (markline.isScanned()) {
            Dialogue.Error(context, R.string.msg_already_scanned);
            return false;
        } else if (Markline.UNGROUPED.equals(markline.Sts)) {
            Dialogue.Error(context, R.string.msg_ungroupped);
            return false;
        } else if (Markline.NOT_CORRECT.equals(markline.Sts)) {
            Dialogue.Error(context, R.string.msg_not_allowed);
            return false;
        } else if (Markline.GRAY_ZONE.equals(markline.Sts) && isModeSingle()) {
            Dialogue.Error(context, R.string.msg_grayzone);
            return false;
        } else if ((parentmark != null) && !parentmark.MarkCode.equals(markline.MarkParent)) {
            Dialogue.Error(context, R.string.msg_pack_wrong);
            return false;
        } else if ((parentmark != null) && (markline.BoxQnty > 1)) {
            Dialogue.Error(context, R.string.msg_scan_item);
            return false;
        }
        return true;
    }

    private boolean updateMarkline(@NonNull Context context, @NonNull Markline markline) {
        if (Markline.UTD.equals(markline.Sts)) {
            markline.Sts = Markline.CHECK_SCAN;

            for (int i = 0; i < marklines.size(); i++) {
                final Markline markchild = marklines.get(i);
                if (markchild.MarkParent.equals(markline.MarkCode)) {
                    markchild.Sts = Markline.CHECK;
                    marklines.set(i, markchild);
                }
            }

            if (markline.MarkParent != null) {
                final int parentpos = findMark(markline.MarkParent);
                final Markline markparent = marklines.get(parentpos);
                markparent.Sts = Markline.UNGROUPED;
                marklines.set(parentpos, markparent);
            }
        } else if (parentmark != null) {
            markline.Sts = Markline.CHECK_SCAN;
            markline.MarkParent = parentmark.MarkCode;
        } else {
            markline.Sts = Markline.TSD;
        }
        return true;
    }

    public int add(@NonNull Context context, @NonNull BarcodeMarker marker) {
        final int markpos = findMark(marker);
        Markline markline = (markpos >= 0 ? marklines.get(markpos) : null);
        if (!checkMarkline(context, markline, document.isReadonly())) {
            return ERROR_VALUE;
        }

        final int position = findLine(marker);
        selectLine(position, markline);
        if (!checkLine(context, marker, (position >= 0))) {
            return ERROR_VALUE;
        }

        if (markpos >= 0) {
            if (!updateMarkline(context, markline)) return ERROR_VALUE;
            marklines.set(markpos, markline);
        } else if (document.isMarked()) {
            markline = new Markline();
            markline.DocName = document.DocName;
            markline.LineID = (int) getMarkId();
            markline.MarkCode = marker.scanned;
            markline.PartIDTH = getPartIDTH(marker);
            markline.Sts = Markline.CR_TSD;
            markline.MarkParent = (parentmark != null ? parentmark.MarkCode : null);
            markline.BoxQnty = (int) dbc.barcodes().getRate(marker.bc);

            if (!checkMarkline(context, markline, true)) return ERROR_VALUE;
            marklines.add(markline);
        }

        if (position >= 0) {
            line.DocQnty += marker.weight;
            line.FactQnty += marker.weight;
            correctDocSumm(line, marker.weight);
        } else if (!updateLine(context, marker)) {
            return ERROR_VALUE;
        }
        return position;
    }
}
