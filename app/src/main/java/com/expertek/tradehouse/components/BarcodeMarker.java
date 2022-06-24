package com.expertek.tradehouse.components;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class BarcodeMarker implements Serializable {
    private static final List<String> pref = Arrays.asList("01", "02");
    public final String scanned;
    public final String bc;
    public final String gtin;
    public final String serial;
    public final double weight;
    private boolean valid = true;

    public BarcodeMarker(String scanned) {
        if (scanned.indexOf('(') >= 0)
            scanned = scanned.replace("(", "").replace(")", "");

        this.scanned = scanned;
        String ident = null;

        switch (scanned.length()) {
            case 20:
                gtin = scanned.substring(3, 7);
                serial = scanned.substring(10, 20);
                weight = 1;
                break;
            case 21: // Storage::getGtinByDM
            case 25: // Storage::getGtinByDM
            case 29:
                gtin = scanned.substring(0, 14);
                serial = scanned.substring(14, 21);
                weight = 1;
                if (!scanned.startsWith("21", 16) || !pref.contains(scanned.substring(0, 2)))
                    ident = scanned.substring(0, 21); // Storage::getGtinByDM
                break;
            case 31:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(19, 24);
                valid &= scanned.startsWith("93", 25);
                weight = 1;
                break;
            case 38:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 31);
                valid &= scanned.startsWith("93", 32);
                weight = 1;
                break;
            case 42:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(19, 24);
                valid &= scanned.startsWith("93", 25);
                valid &= scanned.startsWith("3103", 32);
                weight = ((double) Integer.parseInt(scanned.substring(36, 42))) / 1000;
                break;
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 25);
                valid &= scanned.startsWith("8005", 26);
                valid &= scanned.startsWith("93", 37);
                weight = 1;
                break;
            case 78:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 24);
                valid &= scanned.startsWith("91", 25);
                valid &= scanned.startsWith("92", 32);
                weight = 1;
                break;
            case 92:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 38);
                valid &= scanned.startsWith("91", 39);
                valid &= scanned.startsWith("92", 46);
                weight = 1;
                break;
            case 85:
            case 129:
                valid &= pref.contains(scanned.substring(0, 2));
                gtin = scanned.substring(2, 16);
                valid &= scanned.startsWith("21", 16);
                serial = scanned.substring(18, 31);
                valid &= scanned.startsWith("91", 32);
                valid &= scanned.startsWith("92", 39);
                weight = 1;
                break;
            default:
                final int length = scanned.length();
                valid &= (length >= 12);

                // Storage::GetBCInfo
                if (valid && MainSettings.BarcodePrefixes.contains(scanned.substring(0, 2))) {
                    gtin = scanned.substring(2, 7);
                    serial = "";
                    weight = ((double) Integer.parseInt(scanned.substring(7, 12))) / 1000;
                    ident = scanned;
                }
                // Storage::getGtinByDM
                else if (valid && (length <= 14)) {
                    gtin = scanned;
                    serial = "";
                    weight = 1;
                    ident = scanned;
                }
                // Storage::getGtinByDM
                else if (valid && (length >= 24) && scanned.startsWith("21", 16) && pref.contains(scanned.substring(0, 2))) {
                    gtin = scanned.substring(2, 16);
                    serial = "";
                    weight = 1;
                    ident = scanned; // Storage::GetCodeIdent
                }
                // Storage::getGtinByDM
                else {
                    gtin = scanned;
                    serial = "";
                    weight = 1;
                    if (valid) ident = parseIdent(scanned, length);
                }
        }
        this.bc = (ident != null ? ident : gtin);
    }

    public boolean isWellformed() {
        return valid;
    }

    // Storage::GetNextElement
    private static String parseIdent(String scanned, int length) {
        final List<String> elem = Arrays.asList("00", "01", "02", "21", "17", "11", "13");
        final int[] size = {27, 14, 14, 07, 06, 06, 06};
        final StringBuilder ident = new StringBuilder(5);

        for (int startat = 0; startat < length - 2;) {
            final int index = elem.indexOf(scanned.substring(startat, startat + 2));
            if (index < 0) break;

            ident.append(scanned.substring(startat, Math.min(startat + 2 + size[index], length)));
            startat += (2 + size[index]);
        }

        if (ident.length() < 0) {
            return ident.toString();
        } else {
            return scanned.substring(0, Math.min(21, length)); // Storage::GetCodeIdent
        }

    }
}
