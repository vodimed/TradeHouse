package com.expertek.tradehouse;

import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.barcode;
import com.expertek.tradehouse.dictionaries.entity.good;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;

import java.util.List;

public class BarcodeResolver {
    /**
     * Named barcode representation
     */
    public static class Position {
        protected final barcode barcode;
        protected final good good;

        public Position(barcode barcode, good good) {
            this.barcode = barcode;
            this.good = good;
        }
    }

    public static Position search(String scanned) {
        final DbDictionaries dbc = Application.dictionaries.db();
        final barcode barcode = dbc.barcodes().get(scanned);
        if (barcode == null) return null;
        return new Position(barcode, dbc.goods().get(barcode.GoodsID));
    }

    public static int process(String barcode, document document, List<line> lines) {
        return 0;
    }
}
