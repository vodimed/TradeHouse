package com.expertek.tradehouse.components;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;

import java.util.List;

/**
 * External Model Interface
 */
public interface BarcodeInterface extends Parcelable {
    Document getDocument();
    boolean isMultiposition();
    boolean isEditLine();
    int[] getOpenPos();
    String getBox(); // "Scan for: 10 pts"
    String getUnit();
    double getPrice();
    double getDocQnty();
    double getFactQnty();

    void openDocument(@NonNull Document document, @NonNull List<Line> lines);
    void openGroup(int goodId, @Nullable String unitBC);
    void openLine(int position);
    void setPrice(double value) throws BCException;
    void setDocQnty(double value) throws BCException;
    void setFactQnty(double value) throws BCException;

    void clear();
    void closeLine();
    void clearMarks();
    void commit() throws BCException;
    int scanMark(@NonNull BarcodeMarker marker) throws BCException;

    // Model Exception Class
    class BCException extends Exception {
        public BCException(@StringRes int message) {
            super(Application.app().getResources().getString(message));
        }

        protected BCException(String message) {
            super(message);
        }
    }
}
