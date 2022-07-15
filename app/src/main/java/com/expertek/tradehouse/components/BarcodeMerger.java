package com.expertek.tradehouse.components;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.R;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class BarcodeMerger implements BarcodeInterface {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private final DBDocuments dbd = Application.documents.db();
    private final int[] position = new int[100];
    private Document document = null;
    private List<Line> lines = null;
    public Markline parentmark = null;
    private int poscount = 0;
    private boolean editable = false;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
    public boolean isMultiposition() {
        return (poscount == 1);
    }

    @Override
    public boolean isEditLine() {
        return editable;
    }

    @Override
    public int[] getOpenPos() {
        return Arrays.copyOf(position, poscount);
    }

    @Override
    public String getBox() {
        final String unit = getUnit();
        double boxqnty = 1;

        if (unit == null) {
            return Application.app().getResources().getString(R.string.barcode_box);
        } else {
            return String.format(Locale.getDefault(), "%f %s", boxqnty, unit);
        }
    }

    @Override
    public String getUnit() {
        String result = null;
        for (int i = 0; i < poscount; i++) {
            final String current = lines.get(position[i]).UnitBC;

            if (result == null) {
                result = current;
            } else if (!result.equals(current)) {
                result = null;
                break;
            }
        }
        return result;
    }

    @Override
    public double getPrice() {
        double result = 0;
        for (int i = 0; i < poscount; i++) {
            result += lines.get(position[i]).Price;
        }
        return result / poscount;
    }

    @Override
    public double getDocQnty() {
        double result = 0;
        for (int i = 0; i < poscount; i++) {
            result += lines.get(position[i]).DocQnty;
        }
        return result;
    }

    @Override
    public double getFactQnty() {
        double result = 0;
        for (int i = 0; i < poscount; i++) {
            result += lines.get(position[i]).FactQnty;
        }
        return result;
    }

    @Override
    public void openDocument(@NonNull Document document, @NonNull List<Line> lines) {
        clear();
        this.document = document;
        this.lines = lines;
    }

    @Override
    public void openGroup(int goodId, @Nullable String unitBC) {
        closeLine();
        lines.forEach(new Consumer<Line>() {
            @Override
            public void accept(Line line) {
                if ((line.GoodsID == goodId) && (line.UnitBC.equals(unitBC) || (unitBC == null))) {
                    position[poscount++] = line.Pos - 1;
                }
            }
        });
    }

    @Override
    public void openLine(int position) {
        closeLine();
        this.position[poscount++] = position;
    }

    @Override
    public void setPrice(double value) throws BCException {
        if (isMultiposition())
            throw new BCException(R.string.msg_data_incorrect);
        lines.get(position[0]).Price = value;
    }

    @Override
    public void setDocQnty(double value) throws BCException {
        if (isMultiposition())
            throw new BCException(R.string.msg_data_incorrect);
        lines.get(position[0]).DocQnty = value;
    }

    @Override
    public void setFactQnty(double value) throws BCException {
        if (!isMultiposition()) {
            lines.get(position[0]).FactQnty = value;
        } else {
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void closeLine() {
        poscount = 0;
    }

    @Override
    public void clearMarks() {

    }

    @Override
    public void commit() throws BCException {

    }
}
