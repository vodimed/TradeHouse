package com.expertek.tradehouse.components;

import androidx.annotation.NonNull;

public class BarcodeModel extends BarcodeMerger {
    @Override
    public int scanMark(@NonNull BarcodeMarker marker) throws BCException {
        return 0;
    }
}
